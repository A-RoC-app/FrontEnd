package com.example.myapplication

import android.os.Bundle
import android.widget.Toast // 알림을 위한
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    private lateinit var signupEmail: EditText
    private lateinit var signupPwd: EditText
    private lateinit var signupName: EditText
    private lateinit var signupPhone: EditText
    private lateinit var userTypeRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupEmail = findViewById(R.id.signupEmail)
        signupPwd = findViewById(R.id.signupPwd)
        signupName = findViewById(R.id.signupName)
        signupPhone = findViewById(R.id.signupPhone)
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup)

        // 회원가입 버튼 클릭 이벤트 처리
        val signupButton: Button = findViewById(R.id.signupBtn)
        val backButton : ImageButton = findViewById(R.id.backButton)

        signupButton.setOnClickListener {
            signUp()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        signupEmail.setOnClickListener {clearText(signupEmail) }
        signupPwd.setOnClickListener {clearText(signupPwd) }
        signupName.setOnClickListener {clearText(signupName) }
        signupPhone.setOnClickListener {clearText(signupPhone) }
    }
    fun clearText(editText: EditText){
        editText.setText("")
    }

    private fun signUp() {
        // 입력값 가져오기
        val email = signupEmail.text.toString()
        val password = signupPwd.text.toString()
        val name = signupName.text.toString()
        val phone = signupPhone.text.toString()
        val userType = getUserType()

        // 입력값 유효성 검사
        if (validateInput(email, password, name, phone, userType)) {
            // 로그로 확인
            Log.d("SignupActivity", "Email: $email, Password: $password, Name: $name, Phone: $phone, UserType: $userType")

            // TODO: 서버에 회원가입 요청 및 처리

            // TODO: 회원가입 성공 또는 실패에 따른 처리
        }
    }

    private fun validateInput(email: String, password: String, name: String, phone: String, userType: String): Boolean {
        // 각 필드의 입력값이 비어있는지 확인
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty() || userType.isEmpty()) {
            // 입력되지 않은 필드가 있을 경우 메시지 출력
            showMessage("모든 정보를 입력하세요.")
            return false
        }

        // 이메일 형식 검사
        if (!isEmailValid(email)) {
            showMessage("올바른 이메일 형식이 아닙니다.")
            return false
        }

        // 비밀번호 조건 검사
        if (!isPasswordValid(password)) {
            showMessage("비밀번호는 영문자와 숫자를 포함하여야 합니다.")
            return false
        }
        return true
    }

    private fun showMessage(message: String) {
        // 메시지 출력
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isEmailValid(email: String): Boolean {
        // 이메일 형식 검사를 위한 정규표현식 사용
        val emailPattern = Pattern.compile("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
        return emailPattern.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        // 비밀번호가 영문자와 숫자를 포함하는지 검사
        val letter = Pattern.compile("[a-zA-z]")
        val digit = Pattern.compile("[0-9]")
        return letter.matcher(password).find() && digit.matcher(password).find()
    }

    private fun getUserType(): String {
        // 선택된 RadioButton의 ID를 사용하여 사용자 유형을 반환
        val selectedRadioButtonId = userTypeRadioGroup.checkedRadioButtonId

        if(selectedRadioButtonId == -1) return "" // 선택된 라디오 버튼이 없을때

        val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
        return selectedRadioButton.text.toString()
    }
}
