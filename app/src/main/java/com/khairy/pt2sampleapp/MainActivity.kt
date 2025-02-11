package com.khairy.pt2sampleapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.khairy.pt2sampleapp.databinding.ActivityMainBinding
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startAlternativePaymentMethods
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startCardPayment
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.QuerySdkActivity
import com.payment.paymentsdk.integrationmodels.*
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackQueryInterface
import com.payment.paymentsdk.sharedclasses.model.response.TransactionResponseBody
import com.paytabs.samsungpay.sample.SamsungPayActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), CallbackPaymentInterface, CallbackQueryInterface {

    private var token: String? = null
    private var transRef: String? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.pay.setOnClickListener {
            val configData = generateConfigurationDetails()
            startCardPayment(
                this, configData, this
            )
//            start3DSecureTokenizedCardPayment(
//                this,
//                configData,
//                PaymentSDKSavedCardInfo("4111 11## #### 1111", "visa"),
//                "2C4652BF67A3EA33C6B590FE658078BD",
//                this
//            )

            /*
            **The rest of payment methods
            startTokenizedCardPayment(
                this,
                configData,
                "Token",
                "TransactionRef",
                this
            )
            *
            * */
//            startPaymentWithSavedCards(
//                this,
//                configData,
//                false,
//                this
//            )
        }
        binding.stcPay.setOnClickListener {
            val configData = generateConfigurationDetails(PaymentSdkApms.STC_PAY)
            startAlternativePaymentMethods(this, configData, this)
        }
        binding.samPay.setOnClickListener {
            SamsungPayActivity.start(this, generateConfigurationDetails())
        }
        binding.queryFunction.setOnClickListener {
            QuerySdkActivity.queryTransaction(
                this, PaymentSDKQueryConfiguration(
                    "ServerKey", "ClientKey", "Country Iso 2", "Profile Id", "Transaction Reference"
                ), this
            )
        }
    }

    private fun generateConfigurationDetails(selectedApm: PaymentSdkApms? = null): PaymentSdkConfigurationDetails {
        val profileId = "**********"
        val serverKey = "**********"
        val clientKey = "**********"
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
            "+966568595106",
            "121321",
            "address street",
            ""
        )
        val shippingData = PaymentSdkShippingDetails(
            "City", "SA", "test@test.com", "name1 last1", "+966568595106", "3510", "street2", ""
        )
        val configData = PaymentSdkConfigBuilder(
            profileId, serverKey, clientKey, amount, currency
        ).setCartDescription(cartDesc).setLanguageCode(locale).setBillingData(billingData)
            .setMerchantCountryCode(merchantCountryCode)
            .setTransactionType(PaymentSdkTransactionType.SALE)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM).setShippingData(shippingData)
            .setTokenise(PaymentSdkTokenise.NONE) //Check other tokenizing types in PaymentSdkTokenise
            .setCartId(orderId).showBillingInfo(false).showShippingInfo(false)
            .forceShippingInfo(false).setScreenTitle(transactionTitle)

        if (selectedApm != null) configData.setAlternativePaymentMethods(listOf(selectedApm))/*Check PaymentSdkApms for more payment options*/

        return configData.build()
    }

    override fun onCancel() {
        Toast.makeText(this, "onCancel", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: PaymentSdkError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onResult(transactionResponseBody: TransactionResponseBody) {
        Log.d(TAG, "onResult: $transactionResponseBody")
        Toast.makeText(this, "onResult: $transactionResponseBody", Toast.LENGTH_SHORT).show()
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
    }
}
