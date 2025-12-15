
const regForm = document.getElementById('reg-form');
const loginForm = document.getElementById('login-form');

const regUsername = document.getElementById('reg-username');
const regPassword = document.getElementById('reg-password');

const loginUsername = document.getElementById('login-username');
const loginPassword = document.getElementById('login-password');

function showToast(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
}

function safeParseTextAsJson(text) {
    try { return JSON.parse(text); } catch (e) { return { __rawText: text }; }
}

function sendRegister() {
    const username = regUsername.value.trim();
    const password = regPassword.value;

    if (!username || !password) {
        showToast('Заполните все поля регистрации', 'error');
        return;
    }

    fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
        body: JSON.stringify({ username, password })
    })
        .then(async res => {
            const text = await res.text();
            const payload = safeParseTextAsJson(text);
            if (!res.ok) {
                const err = payload.error || payload.username || payload.__rawText || 'Ошибка регистрации';
                throw new Error(err);
            }
            return payload;
        })
        .then(payload => {
            if (payload.username && payload.username !== 'ERROR') {
                try { sessionStorage.setItem('chat_nickname', payload.username); } catch (e) {}
                window.location.href = '/index.html';
            } else {
                showToast('Регистрация не удалась', 'error');
            }
        })
        .catch(err => showToast(err.message, 'error'));
}

function sendLogin() {
    const username = loginUsername.value.trim();
    const password = loginPassword.value;

    if (!username || !password) {
        showToast('Заполните все поля входа', 'error');
        return;
    }

    fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
        body: JSON.stringify({ username, password })
    })
        .then(async res => {
            const text = await res.text();
            const payload = safeParseTextAsJson(text);
            if (!res.ok) {
                const err = payload.error || payload.username || payload.__rawText || 'Ошибка входа';
                throw new Error(err);
            }
            return payload;
        })
        .then(payload => {
            if (payload.username && payload.username !== 'ERROR') {
                try { sessionStorage.setItem('chat_nickname', payload.username); } catch (e) {}
                window.location.href = '/index.html';
            } else {
                showToast('Неверный логин или пароль', 'error');
            }
        })
        .catch(err => showToast(err.message, 'error'));
}

if (regForm) {
    regForm.addEventListener('submit', function(e) {
        e.preventDefault();
        sendRegister();
    });
}

if (loginForm) {
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        sendLogin();
    });
}