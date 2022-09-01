package com.khairy.pt2sampleapp

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.khairy.pt2sampleapp.databinding.ActivityMainBinding
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startAlternativePaymentMethods
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startCardPayment
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.*
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.paytabs.samsungpay.sample.SamsungPayActivity

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), CallbackPaymentInterface {
    private var token: String? = null
    private var transRef: String? = null
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)
        b.pay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails()
            startCardPayment(this, configData, this)
        }
        b.stcPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails(PaymentSdkApms.STC_PAY)
            startAlternativePaymentMethods(this, configData, this)
        }

        b.samPay.setOnClickListener {
            SamsungPayActivity.start(this, generatePaytabsConfigurationDetails())
        }



    }


    private fun generatePaytabsConfigurationDetails(selectedApm: PaymentSdkApms? = null): PaymentSdkConfigurationDetails {
        val profileId = "42007"
        val serverKey = "STJNLJWLDL-JBJRGGBRBD-6NHBMHTKMM"
        val clientKey = "CKKMD9-HQVQ62-6RTT2R-GRMP2B"
        val locale = PaymentSdkLanguageCode.EN /*Or PaymentSdkLanguageCode.AR*/
        val transactionTitle = "Test SDK"
        val orderId = "123456"
        val cartDesc = "Cart description"
        val currency = "SAR"
        val amount = 20.0
        val merchantCountryCode = "SA"
        val billingData = PaymentSdkBillingDetails(
            "City",
            "SA",
            "email1@domain.com",
            "name name",
            "+966568595106", "121321",
            "address street", ""
        )
        val shippingData = PaymentSdkShippingDetails(
            "City",
            "SA",
            "test@test.com",
            "name1 last1",
            "+966568595106", "3510",
            "street2", ""
        )
        val configData = PaymentSdkConfigBuilder(
            profileId,
            serverKey,
            clientKey, amount, currency
        )
            .setCartDescription(cartDesc)
            .setLanguageCode(locale)
            .setBillingData(billingData)
            .setMerchantCountryCode(merchantCountryCode)
            .setTransactionType(PaymentSdkTransactionType.SALE)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM)
            .setShippingData(shippingData)
            .setTokenise(PaymentSdkTokenise.NONE) //Check other tokenizing types in PaymentSdkTokenise
            .setCartId(orderId)
            .showBillingInfo(false)
            .showShippingInfo(false)
            .forceShippingInfo(false)
            .setScreenTitle(transactionTitle)

        if (selectedApm != null)
            configData.setAlternativePaymentMethods(listOf(selectedApm))
        /*Check PaymentSdkApms for more payment options*/

        return configData.build()
    }

    override fun onError(error: PaymentSdkError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentCancel() {
        Toast.makeText(this, "onPaymentCancel", Toast.LENGTH_SHORT).show()

    }

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        Log.d(TAG, "Did payment success?: ${paymentSdkTransactionDetails.isSuccess}")
        token = paymentSdkTransactionDetails.token
        transRef = paymentSdkTransactionDetails.transactionReference
        Toast.makeText(
            this,
            paymentSdkTransactionDetails.paymentResult?.responseMessage ?: "PaymentFinish",
            Toast.LENGTH_SHORT
        ).show()
    }}
