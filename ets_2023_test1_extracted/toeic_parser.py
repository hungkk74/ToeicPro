import re
import sys
import argparse

def escape_sql(text):
    if text is None:
        return ""
    return text.replace("'", "''").strip()

def parse_toeic_data(input_file, output_file, exam_name):
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    sql_statements = []
    
    # 1. Setup Data
    sql_statements.append("SET FOREIGN_KEY_CHECKS = 0;")
    sql_statements.append("TRUNCATE TABLE question;")
    sql_statements.append("TRUNCATE TABLE question_group;")
    sql_statements.append("TRUNCATE TABLE part;")
    sql_statements.append("TRUNCATE TABLE exam;")
    sql_statements.append("SET FOREIGN_KEY_CHECKS = 1;")
    sql_statements.append("")
    
    exam_id = 1
    sql_statements.append(f"INSERT INTO exam (id, name, type) VALUES ({exam_id}, '{escape_sql(exam_name)}', 'READING');")
    
    # 2. Extract Parts
    parts = re.finditer(r'==================================================\nPART:\s*(\d+)\nTYPE:\s*([A-Z_]+)\n==================================================', content)
    
    part_id_map = {}
    for match in parts:
        part_num = int(match.group(1))
        part_type = match.group(2)
        part_id_map[part_num] = part_num # Mapping part_num to part_id
        
        name = f"Part {part_num}: {part_type.replace('_', ' ').title()}"
        sql_statements.append(f"INSERT INTO part (id, exam_id, name, type) VALUES ({part_num}, {exam_id}, '{escape_sql(name)}', 'PART_{part_num}');")

    sql_statements.append("")

    # 3. Extract Passages (Question Groups)
    passage_pattern = r'\[PASSAGE_START\](.*?)\[PASSAGE_END\]'
    passages = re.finditer(passage_pattern, content, re.DOTALL)
    
    group_id_counter = 1
    passage_id_to_db_id = {}
    
    for match in passages:
        block = match.group(1)
        
        passage_id_match = re.search(r'\[PASSAGE_ID\]:\s*(.*)', block)
        passage_content_match = re.search(r'\[PASSAGE_CONTENT\]:\n(.*?)$', block, re.DOTALL)
        
        if passage_id_match and passage_content_match:
            passage_id = passage_id_match.group(1).strip()
            passage_text = escape_sql(passage_content_match.group(1))
            
            # Giả định passage thuộc Part 6 hoặc Part 7 dựa vào ID (ví dụ: P6_G1 -> Part 6)
            part_match = re.search(r'P(\d+)', passage_id)
            part_num = int(part_match.group(1)) if part_match else 7
            
            db_group_id = group_id_counter
            passage_id_to_db_id[passage_id] = db_group_id
            
            sql_statements.append(f"INSERT INTO question_group (id, part_id, passage_text) VALUES ({db_group_id}, {part_num}, '{passage_text}');")
            group_id_counter += 1

    sql_statements.append("")

    # 4. Extract Questions
    item_pattern = r'\[ITEM_START\](.*?)\[ITEM_END\]'
    items = re.finditer(item_pattern, content, re.DOTALL)
    
    for match in items:
        block = match.group(1)
        
        q_num = re.search(r'\[Q_NUM\]:\s*(\d+)', block).group(1)
        question_text = escape_sql(re.search(r'\[QUESTION\]:\s*(.*)', block).group(1))
        
        op_a = escape_sql(re.search(r'\[OPTION_A\]:\s*(.*)', block).group(1))
        op_b = escape_sql(re.search(r'\[OPTION_B\]:\s*(.*)', block).group(1))
        op_c = escape_sql(re.search(r'\[OPTION_C\]:\s*(.*)', block).group(1))
        op_d = escape_sql(re.search(r'\[OPTION_D\]:\s*(.*)', block).group(1))
        answer = escape_sql(re.search(r'\[ANSWER\]:\s*([A-D])', block).group(1))
        
        # Optional fields
        explanation_match = re.search(r'\[EXPLANATION\]:\s*(.*)', block)
        explanation = f"'{escape_sql(explanation_match.group(1))}'" if explanation_match else "NULL"
        
        # Determine Part based on question number (Standard TOEIC reading)
        q_num_int = int(q_num)
        if 101 <= q_num_int <= 130:
            part_id = 5
        elif 131 <= q_num_int <= 146:
            part_id = 6
        else:
            part_id = 7
            
        passage_ref_match = re.search(r'\[PASSAGE_REF\]:\s*(.*)', block)
        group_id = "NULL"
        if passage_ref_match:
            passage_ref = passage_ref_match.group(1).strip()
            if passage_ref in passage_id_to_db_id:
                group_id = passage_id_to_db_id[passage_ref]
                
        sql = f"INSERT INTO question (question_number, content, option_a, option_b, option_c, option_d, correct_option, explanation, part_id, question_group_id) VALUES ({q_num}, '{question_text}', '{op_a}', '{op_b}', '{op_c}', '{op_d}', '{answer}', {explanation}, {part_id}, {group_id});"
        sql_statements.append(sql)

    # Write output
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_statements) + '\n')
        
    print(f"Hoàn tất! Đã trích xuất {len(sql_statements) - 8} bản ghi vào file: {output_file}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Parse TOEIC Custom Tagged Markup to SQL Inserts.')
    parser.add_argument('--input', required=True, help='Đường dẫn file text đầu vào (vd: de_thi_text.txt)')
    parser.add_argument('--output', default='import_data.sql', help='Đường dẫn file SQL đầu ra (mặc định: import_data.sql)')
    parser.add_argument('--exam', default='ETS 2023 TEST 01', help='Tên của bài thi')
    
    args = parser.parse_args()
    
    parse_toeic_data(args.input, args.output, args.exam)
