import { NextRequest, NextResponse } from 'next/server';
import fs from 'fs';
import path from 'path';

export async function GET(
  _request: NextRequest,
  { params }: { params: { path?: string[] } }
) {
  try {
    const segments = params.path || [];
    const filePath = path.join(process.cwd(), 'public', 'audio', ...segments);

    if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
      const fileBuffer = fs.readFileSync(filePath);
      return new NextResponse(fileBuffer, {
        headers: {
          'Content-Type': 'audio/mpeg',
          'Content-Length': fileBuffer.length.toString(),
        },
      });
    }
  } catch {
    // Fallback 404
  }

  return new NextResponse('Audio file not found', { status: 404 });
}
