package com.kodonho.aroc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.app.AlertDialog

class Main2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val loginButton = findViewById<Button>(R.id.login_btn)
        val registerButton = findViewById<Button>(R.id.sign_btn)
        val kakaoButton = findViewById<Button>(R.id.kakao_btn)

        loginButton.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            Log.d("SignupActivity", "Register button clicked")
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        kakaoButton.setOnClickListener {
            Log.d("Main2Activity", "Kakao button clicked")

            showUserTypeRegistrationPopup()
        } // 추후 수정 필요
    }
    private fun showUserTypeRegistrationPopup() {
        // 팝업을 표시하는 코드 작성
        // 예를 들어, AlertDialog를 사용하여 팝업을 표시할 수 있습니다.
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("회원정보 수정")
        alertDialogBuilder.setMessage("회원 유형을 등록해주세요.")
        alertDialogBuilder.setPositiveButton("확인") { dialog, _ ->
            // 팝업에서 확인 버튼을 클릭하면 수행할 작업 작성
            dialog.dismiss()
            // TODO: 회원정보 수정 페이지로 이동하는 코드 추가 필요
            // ex : val intent = Intent(this, UserInfoEditActivity::class.java) startActivity(intent)
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
