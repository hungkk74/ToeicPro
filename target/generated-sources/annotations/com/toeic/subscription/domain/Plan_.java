package com.toeic.subscription.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;

/**
 * Static metamodel for {@link com.toeic.subscription.domain.Plan}
 **/
@StaticMetamodel(Plan.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Plan_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #code
	 **/
	public static final String CODE = "code";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #price
	 **/
	public static final String PRICE = "price";
	
	/**
	 * @see #durationDays
	 **/
	public static final String DURATION_DAYS = "durationDays";
	
	/**
	 * @see #features
	 **/
	public static final String FEATURES = "features";
	
	/**
	 * @see #isActive
	 **/
	public static final String IS_ACTIVE = "isActive";

	
	/**
	 * Static metamodel type for {@link com.toeic.subscription.domain.Plan}
	 **/
	public static volatile EntityType<Plan> class_;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Plan#id}
	 **/
	public static volatile SingularAttribute<Plan, Long> id;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Plan#code}
	 **/
	public static volatile SingularAttribute<Plan, String> code;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Plan#name}
	 **/
	public static volatile SingularAttribute<Plan, String> name;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Plan#price}
	 **/
	public static volatile SingularAttribute<Plan, BigDecimal> price;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Plan#durationDays}
	 **/
	public static volatile SingularAttribute<Plan, Integer> durationDays;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Plan#features}
	 **/
	public static volatile SingularAttribute<Plan, String> features;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Plan#isActive}
	 **/
	public static volatile SingularAttribute<Plan, Boolean> isActive;

}

