function login() {
    const usernameInput = document.getElementById("username");
    const username = usernameInput.value;

    const passwordInput = document.getElementById("password");
    const password = passwordInput.value;

    const usernameErrorSpan = document.getElementById("usernameErrorSpan");
    const usernamePasswordError = document.getElementById("usernamePasswordError");

    hasError = false;
    if (username.length === 0 && password.length === 0){
        usernameErrorSpan.style.display = "block";
        passwordInput.nextElementSibling.style.display = "block";
        document.getElementById("username").style.borderColor = "red";
        document.getElementById("password").style.borderColor = "red";
        hasError = true;
    }

    if (username === null || username === 'undefined' || username.length === 0){
        usernameErrorSpan.style.display = "block";
        document.getElementById("username").style.borderColor = "red";
        usernameErrorSpan.textContent = "Username kiritilmadi";
        hasError = true
    }

    if (password === null || password === 'undefined' || password.length === 0){
        document.getElementById("password").style.borderColor = "red";
        passwordInput.nextElementSibling.style.display = "block";
        hasError = true;
    }
    if (hasError){
        return;
    }
    usernameErrorSpan.style.display = "none";
    passwordInput.nextElementSibling.style.display = "none";
    document.getElementById("username").style.color = "";
    document.getElementById("username").style.borderColor = "#ddd";
    document.getElementById("password").style.color = "";
    document.getElementById("password").style.borderColor = "#ddd";

    const body = {
        "username" : username,
        "password" : password
    }

    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/auth/login",{
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
                usernameErrorSpan.textContent = "Username already exists";
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            localStorage.setItem("userDetail", JSON.stringify(data));
            localStorage.setItem("jwtToken", data.jwt);
            // sahifani ortga qaytganda kiritilgan qiymatlar o'chirilyapti
            passwordInput.value = '';
            usernameInput.value = '';
            window.location.href = "./profile-post-list.html"
        })
        .catch(error =>{
            error.then(errorMessage =>{
                usernamePasswordError.style.display = "block";
                usernamePasswordError.textContent = errorMessage.toString();
            })
        })
}