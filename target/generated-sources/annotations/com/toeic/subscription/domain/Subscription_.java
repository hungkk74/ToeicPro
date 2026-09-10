package com.toeic.subscription.domain;

import com.toeic.subscription.domain.enumeration.SubscriptionStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

/**
 * Static metamodel for {@link com.toeic.subscription.domain.Subscription}
 **/
@StaticMetamodel(Subscription.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Subscription_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #userId
	 **/
	public static final String USER_ID = "userId";
	
	/**
	 * @see #status
	 **/
	public static final String STATUS = "status";
	
	/**
	 * @see #startsAt
	 **/
	public static final String STARTS_AT = "startsAt";
	
	/**
	 * @see #expiresAt
	 **/
	public static final String EXPIRES_AT = "expiresAt";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #plan
	 **/
	public static final String PLAN = "plan";

	
	/**
	 * Static metamodel type for {@link com.toeic.subscription.domain.Subscription}
	 **/
	public static volatile EntityType<Subscription> class_;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Subscription#id}
	 **/
	public static volatile SingularAttribute<Subscription, Long> id;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Subscription#userId}
	 **/
	public static volatile SingularAttribute<Subscription, String> userId;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Subscription#status}
	 **/
	public static volatile SingularAttribute<Subscription, SubscriptionStatus> status;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Subscription#startsAt}
	 **/
	public static volatile SingularAttribute<Subscription, Instant> startsAt;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Subscription#expiresAt}
	 **/
	public static volatile SingularAttribute<Subscription, Instant> expiresAt;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Subscription#createdAt}
	 **/
	public static volatile SingularAttribute<Subscription, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.Subscription#plan}
	 **/
	public static volatile SingularAttribute<Subscription, Plan> plan;

}

