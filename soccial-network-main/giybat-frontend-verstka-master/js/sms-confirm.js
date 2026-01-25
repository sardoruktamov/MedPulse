const input1 = document.getElementById('input1');
const input2 = document.getElementById('input2');
const input3 = document.getElementById('input3');
const input4 = document.getElementById('input4');
const input5 = document.getElementById('input5');
const errorSmsCode = document.getElementById("errorSmsCode")
const userPhone = localStorage.getItem("userPhoneNumber");
document.getElementById('userPhone').textContent = userPhone;

function handleInput(event) {
    const enteredValue = event.target.value;
    const elementId = event.target.id;
    const nextInput = event.target.nextElementSibling;
    if (enteredValue && nextInput){
        nextInput.focus()
    }else if (nextInput === null){
        handleSubmit();
    }
    console.log(nextInput)
}

function handleSubmit() {
    // send confirm request
    let code = 0;
    if (input1.value && input2.value && input3.value && input4.value && input5.value){
        code = input1.value + input2.value + input3.value + input4.value + input5.value;
        console.log(code)
    }
    if (code == null || userPhone == null){  // TODO qiymat kiritilmasa buttonni bosilmaydigan qilib qo'yish kerak
        return;
    }

    const body = {
        "phoneNumber" : userPhone,
        "code" : code
    }
    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/auth/registration/sms-verification",{
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
                errorSmsCode.textContent = "SMS codi xato";
                console.log("1-then-----SMS codi xato")
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            localStorage.setItem("userDetail", JSON.stringify(data));
            localStorage.setItem("jwtToken", data.jwt);
            localStorage.removeItem("userPhoneNumber");
            window.location.href = "./profile-post-list.html";
            clearInput();
        })
        .catch(error =>{
            error.then(errorMessage =>{
                errorSmsCode.style.display = "block";
                errorSmsCode.textContent = errorMessage.toString();
                console.log(errorMessage)
                console.log("CATCH-----SMS codi xato")
            })
        })

}

// sms ni qayta jo'natish
function resendSms() {
    const body = {
        "phoneNumber" : userPhone
    }
    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/auth/registration/sms-verification-resent",{
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
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            console.log(data)
            alert(data.message)
        })
        .catch(error =>{
            error.then(errorMessage =>{
                errorSmsCode.style.display = "block";
                errorSmsCode.textContent = errorMessage.toString();
                console.log(errorMessage)
            })
        })
}

function clearInput() {
    input1.value = '';
    input2.value = '';
    input3.value = '';
    input4.value = '';
    input5.value = '';
    document.getElementById('userPhone').textContent = '***';
}

input1.addEventListener('input', handleInput);
input2.addEventListener('input', handleInput);
input3.addEventListener('input', handleInput);
input4.addEventListener('input', handleInput);
input5.addEventListener('input', handleInput);