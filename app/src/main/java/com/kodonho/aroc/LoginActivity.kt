package com.kodonho.aroc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kodonho.aroc.RetrofitInstance.RetrofitBuilder
import com.kodonho.aroc.api.AuthService
import com.kodonho.aroc.api.MemberView
import com.kodonho.aroc.dto.MemberViewDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var authService: AuthService
    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        RetrofitBuilder.initialize(applicationContext) // 초기화

        emailEditText = findViewById(R.id.loginEmail)
        passwordEditText = findViewById(R.id.loginPwd)

        val loginButton: Button = findViewById(R.id.loginBtn)
        val backButton: ImageButton = findViewById(R.id.backButton)

        authService = RetrofitBuilder.createService(AuthService::class.java)

        loginButton.setOnClickListener {
            login()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        emailEditText.setOnClickListener {
            clearEditText(emailEditText)
        }

        passwordEditText.setOnClickListener {
            clearEditText(passwordEditText)
        }
    }

    private fun clearEditText(editText: EditText) {
        editText.setText("")
    }

    private fun login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (validateInput(email, password)) {
            Log.d(TAG, "Email: $email, Password: $password")
            val loginService = RetrofitBuilder.createService(AuthService::class.java)

            loginService.Login(email,password).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                            // 확인용
                            val cookies = response.headers()["Set-Cookie"]
                            Log.d(TAG, "Set-Cookie: $cookies")
                            getMemberInfo(email)
                        } else {
                            Toast.makeText(this@LoginActivity, "응답 데이터가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun getMemberInfo(email: String) {
        val memberViewService = RetrofitBuilder.createService(MemberView::class.java)

        memberViewService.getMemberInfo(email).enqueue(object : Callback<MemberViewDto> {
            override fun onResponse(call: Call<MemberViewDto>, response: Response<MemberViewDto>) {
                if (response.isSuccessful) {
                    val memberViewDto = response.body()
                    if (memberViewDto != null) {
                        val userType = memberViewDto.userType
                        Log.d(TAG, "UserType: $userType") // userType 로그 출력
                        when (userType) {
                            0 -> { // 보호자
                                startActivity(Intent(this@LoginActivity, MapSearch::class.java)) // 나중에 바꿔야 함!
                            }
                            1 -> { // 사용자
                                startActivity(Intent(this@LoginActivity, UserListView::class.java))
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, "알 수 없는 사용자 유형입니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "사용자 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberViewDto>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("모든 정보를 입력하세요.")
            return false
        }
        if (!isEmailValid(email)) {
            showMessage("올바른 이메일 형식이 아닙니다.")
            return false
        }
        if (!isPasswordValid(password)) {
            showMessage("비밀번호는 영문자와 숫자를 포함하여야 합니다.")
            return false
        }
        return true
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Pattern.compile("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
        return emailPattern.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        val letter = Pattern.compile("[a-zA-Z]")
        val digit = Pattern.compile("[0-9]")
        return letter.matcher(password).find() && digit.matcher(password).find()
    }
}
