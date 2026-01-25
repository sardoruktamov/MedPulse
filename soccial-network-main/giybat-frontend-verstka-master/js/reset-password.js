function resetPassword() {
    const usernameInput = document.getElementById("username");
    const username = usernameInput.value;
    const usernameErrorSpan = document.getElementById("usernameErrorSpan")
    if (!username) {
        return;
    }

    const body = {
        "username" : username
    }

    const lang = document.getElementById("current-lang").textContent


    fetch("http://localhost:8080/api/v1/auth/registration/reset-password",{
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
                usernameErrorSpan.style.display = "block";
                document.getElementById("username").style.borderColor = "red";
                document.getElementById("username").style.color = "red";
                usernameErrorSpan.textContent = response.toString();
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            alert(data.message)
            usernameErrorSpan.style.display = "none";
            document.getElementById("username").style.borderColor = "#ddd";
            document.getElementById("username").style.color = "";

            const emailOrPhone = checkEmailOrPhone(username);
            if (emailOrPhone === 'Email'){    // email
                localStorage.setItem("username", username);
                window.location.href = "./reset-password-confirm.html";
            }else if (emailOrPhone === 'Phone'){  // phone
                localStorage.setItem("username", username);
                window.location.href = "./sms-confirm.html"
            }
        })
        .catch(error =>{
            error.then(errorMessage =>{
                usernameErrorSpan.style.display = "block";
                usernameErrorSpan.textContent = errorMessage.toString();
            })
        })
}

function checkEmailOrPhone(value) {
    // Regular expression for validating email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    // Regular expression for validating phone numbers
    // Adjust based on your requirements (e.g., country-specific formats)
    const phoneRegex = /^998\d{9}$/; // 998 91 572 1213

    if (emailRegex.test(value)) {
        return "Email";
    } else if (phoneRegex.test(value)) {
        return "Phone";
    } else {
        return "Invalid";
    }
}