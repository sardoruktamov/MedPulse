function resetPasswordConfirm() {
    const confirmCodeValue = document.getElementById("confirm_code").value;
    const newPasswordValue = document.getElementById("new_password").value;
    const username = localStorage.getItem("username");
    const confirm_codeErrorSpan = document.getElementById("confirm_codeErrorSpan")

    if (!confirmCodeValue || !newPasswordValue || !username) {
        alert("Please fill all inputs");
        return;
    }

    const body = {
        "username" : username,
        "confirmCode" : confirmCodeValue,
        "password" : newPasswordValue
    }

    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/auth/registration/reset-password-confirm",{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
        },
        body: JSON.stringify(body)

    })
        .then(response =>{
            if (response.ok){
                return response.json()
            }else {
                confirm_codeErrorSpan.style.display = "block";
                console.log(response)
                document.getElementById("confirmCodeValue").style.borderColor = "red";
                document.getElementById("confirmCodeValue").style.color = "red";
                confirm_codeErrorSpan.textContent = response.toString();
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            alert(data.message)
            confirm_codeErrorSpan.style.display = "none";
            document.getElementById("confirmCodeValue").style.borderColor = "#ddd";
            document.getElementById("confirmCodeValue").style.color = "";
            window.location.href = "./login.html";
            confirmCodeValue.textContent = '';
            newPasswordValue.textContent = '';

        })
        .catch(error =>{
            error.then(errorMessage =>{
                confirm_codeErrorSpan.style.display = "block";
                confirm_codeErrorSpan.textContent = errorMessage.toString();
            })
        })
}

