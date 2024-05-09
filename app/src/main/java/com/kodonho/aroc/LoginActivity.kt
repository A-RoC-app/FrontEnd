package com.kodonho.aroc

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.loginEmail)
        passwordEditText = findViewById(R.id.loginPwd)

        val loginButton: Button = findViewById(R.id.loginBtn)
        val backButton : ImageButton = findViewById(R.id.backButton)

        loginButton.setOnClickListener {
            login()
        }

        backButton.setOnClickListener(){
            onBackPressed()
        }

        emailEditText.setOnClickListener {clearText(emailEditText)}
        passwordEditText.setOnClickListener {clearText(passwordEditText) }
    }

    fun clearText(editText: EditText){
        editText.setText("")
    }
    // 로그인 버튼 클릭 이벤트 처리
    fun login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // 이메일 또는 비밀번호 중 하나라도 입력되지 않았을 경우
        if (email.isEmpty() || password.isEmpty()) {
            // Toast 알림 띄우기
            showToast("이메일 또는 비밀번호를 입력하세요.")
            return
        }

        // 입력된 이메일과 비밀번호 출력
        Log.d("Credentials", "Email: $email, Password: $password")

        // 예를 들어, 여기서 서버에 로그인 요청을 보내고 결과에 따라 처리할 수 있습니다.
    }

    // Toast 알림 표시 함수
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
