package com.toeic.subscription.domain;

import com.toeic.subscription.domain.enumeration.PaymentGateway;
import com.toeic.subscription.domain.enumeration.PaymentStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Static metamodel for {@link com.toeic.subscription.domain.PaymentTransaction}
 **/
@StaticMetamodel(PaymentTransaction.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PaymentTransaction_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #orderCode
	 **/
	public static final String ORDER_CODE = "orderCode";
	
	/**
	 * @see #gateway
	 **/
	public static final String GATEWAY = "gateway";
	
	/**
	 * @see #gatewayTransId
	 **/
	public static final String GATEWAY_TRANS_ID = "gatewayTransId";
	
	/**
	 * @see #amount
	 **/
	public static final String AMOUNT = "amount";
	
	/**
	 * @see #status
	 **/
	public static final String STATUS = "status";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #subscription
	 **/
	public static final String SUBSCRIPTION = "subscription";

	
	/**
	 * Static metamodel type for {@link com.toeic.subscription.domain.PaymentTransaction}
	 **/
	public static volatile EntityType<PaymentTransaction> class_;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#id}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, Long> id;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#orderCode}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, String> orderCode;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#gateway}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, PaymentGateway> gateway;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#gatewayTransId}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, String> gatewayTransId;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#amount}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, BigDecimal> amount;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#status}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, PaymentStatus> status;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#createdAt}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link com.toeic.subscription.domain.PaymentTransaction#subscription}
	 **/
	public static volatile SingularAttribute<PaymentTransaction, Subscription> subscription;

}

