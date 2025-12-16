// var stompClient = null;
// var username = null;
//
// function initNicknameFromSession() {
//     try {
//         const stored = sessionStorage.getItem('chat_nickname');
//         if (stored) {
//             username = stored;
//             const nickInput = document.querySelector("#nickname_input_value");
//             if (nickInput) {
//                 nickInput.value = username;
//                 nickInput.setAttribute('readonly', 'readonly');
//             }
//         }
//     } catch (e) {
//         console.warn('Не удалось получить nickname из sessionStorage', e);
//     }
// }
//
// function connect() {
//     if (!username) {
//         const input = document.querySelector("#nickname_input_value");
//         username = input ? input.value.trim() : null;
//     }
//     if (!username) {
//         alert("Введите никнейм!");
//         return;
//     }
//
//     if (stompClient && stompClient.connected) {
//         console.log("already connected");
//         return;
//     }
//
//     var socket = new SockJS('/websocket');
//     stompClient = Stomp.over(socket);
//     stompClient.debug = null;
//
//     stompClient.connect({}, function(frame) {
//         console.log("connected " + frame);
//         stompClient.subscribe('/topic/messages', function(response) {
//             const body = response && response.body;
//             if (!body) return;
//             const firstChar = body.trim()[0];
//             if (firstChar !== '{' && firstChar !== '[') {
//                 console.warn('Non-JSON WS message:', body);
//                 return;
//             }
//             try {
//                 var data = JSON.parse(body);
//                 draw("left", data.message, data.username);
//             } catch (e) {
//                 console.error('Failed to parse WS JSON:', e, body);
//             }
//         });
//         stompClient.send("/app/addUser", {}, JSON.stringify({
//             'username': username,
//             'message': username + " joined the chat"
//         }));
//     }, function(error) {
//         console.error('STOMP connect error', error);
//         stompClient = null;
//         // можно добавить авто‑переподключение при желании
//     });
// }
//
// function draw(side, text, username) {
//     console.log("drawing..");
//     var $message;
//     $message = $($('.message_template').clone().html());
//     $message.addClass(side).find('.text').html(text);
//     $message.find('.username').html(username);
//     $('.messages').append($message);
//     return setTimeout(function () {
//         return $message.addClass('appeared');
//     }, 0);
// }
//
// function disconnect() {
//     if (stompClient) {
//         stompClient.disconnect();
//         stompClient = null;
//     }
// }
//
// function sendMessage() {
//     const message = $("#message_input_value").val();
//     const nick = username || $("#nickname_input_value").val();
//     if (!message || !nick) {
//         alert('Введите сообщение и никнейм');
//         return;
//     }
//     stompClient.send("/app/message", {}, JSON.stringify({
//         'message': message,
//         'username': nick
//     }));
//     $("#message_input_value").val('');
// }
// document.addEventListener('DOMContentLoaded', () => {
//     initNicknameFromSession();
//
//     // Элементы
//     const uploadBtn = document.getElementById('uploadBtn');
//     const fileInput = document.getElementById('fileInput');
//
//     // Привязка обработчика загрузки (если элемент есть)
//     if (uploadBtn && fileInput) {
//         uploadBtn.addEventListener('click', async () => {
//             const file = fileInput.files[0];
//             if (!file) return alert('Выберите файл');
//             const form = new FormData();
//             form.append('file', file);
//             const resp = await fetch('/api/files', { method: 'POST', body: form, credentials: 'same-origin' });
//             if (!resp.ok) {
//                 const text = await resp.text();
//                 return alert('Upload failed: ' + text);
//             }
//         });
//     }
//
//
//
// });
// document.addEventListener('DOMContentLoaded', function() {
//     initNicknameFromSession();
//     // Автоподключение, если ник уже есть (после логина)
//     if (sessionStorage.getItem('chat_nickname')) {
//         connect();
//     }
// });
// /js/script.js

// Globals
var stompClient = null;
var username = null;

// Utility: escape HTML to avoid XSS when inserting filenames/user input
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"'`=\/]/g, function (s) {
        return ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;',
            '/': '&#x2F;',
            '`': '&#x60;',
            '=': '&#x3D;'
        })[s];
    });
}

// Initialize nickname from sessionStorage if present
function initNicknameFromSession() {
    try {
        const stored = sessionStorage.getItem('chat_nickname');
        if (stored) {
            username = stored;
            const nickInput = document.querySelector("#nickname_input_value");
            if (nickInput) {
                nickInput.value = username;
                nickInput.setAttribute('readonly', 'readonly');
            }
        }
    } catch (e) {
        console.warn('Не удалось получить nickname из sessionStorage', e);
    }
}

// Connect to STOMP over SockJS and subscribe to topic
function connect() {
    if (!username) {
        const input = document.querySelector("#nickname_input_value");
        username = input ? input.value.trim() : null;
    }
    if (!username) {
        alert("Введите никнейм!");
        return;
    }

    if (stompClient && stompClient.connected) {
        console.log("already connected");
        return;
    }

    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function(frame) {
        console.log("connected", frame);
        // Subscribe to messages; handle both text messages and file notifications
        stompClient.subscribe('/topic/messages', function(response) {
            const body = response && response.body;
            if (!body) return;
            const firstChar = body.trim()[0];
            if (firstChar !== '{' && firstChar !== '[') {
                console.warn('Non-JSON WS message:', body);
                return;
            }
            try {
                var data = JSON.parse(body);
                // If message is a file notification
                if (data.type === 'file') {
                    // If server provided URL, render link; otherwise render download button that calls downloadFile
                    if (data.url) {
                        const safeName = escapeHtml(data.filename);
                        const linkHtml = `<a href="${escapeHtml(data.url)}" download="${safeName}">${safeName}</a>`;
                        draw("left", linkHtml, data.uploader || '');
                    } else if (data.fileId) {
                        const safeName = escapeHtml(data.filename || ('file_' + data.fileId));
                        const btnHtml = `<button class="btn btn-link p-0" onclick="downloadFile(${data.fileId}, '${safeName}')">Скачать ${safeName}</button>`;
                        draw("left", btnHtml, data.uploader || '');
                    } else {
                        draw("left", "File uploaded", data.uploader || '');
                    }
                } else {
                    // Regular chat message
                    draw("left", escapeHtml(data.message || ''), escapeHtml(data.username || ''));
                }
            } catch (e) {
                console.error('Failed to parse WS JSON:', e, body);
            }
        });

        // Notify server about new user
        stompClient.send("/app/addUser", {}, JSON.stringify({
            'username': username,
            'message': username + " joined the chat"
        }));
    }, function(error) {
        console.error('STOMP connect error', error);
        stompClient = null;
    });
}

// Render message into chat
function draw(side, textHtml, usernameText) {
    var $message = $($('.message_template').clone().html());
    $message.addClass(side).find('.text').html(textHtml);
    $message.find('.username').html(escapeHtml(usernameText || ''));
    $('.messages').append($message);
    setTimeout(function () { $message.addClass('appeared'); }, 0);
}

// Disconnect STOMP
function disconnect() {
    if (stompClient) {
        stompClient.disconnect();
        stompClient = null;
    }
}

// Send chat message
function sendMessage() {
    const message = $("#message_input_value").val();
    const nick = username || $("#nickname_input_value").val();
    if (!message || !nick) {
        alert('Введите сообщение и никнейм');
        return;
    }
    if (!stompClient || !stompClient.connected) {
        alert('Not connected to chat');
        return;
    }
    stompClient.send("/app/message", {}, JSON.stringify({
        'message': message,
        'username': nick
    }));
    $("#message_input_value").val('');
}

// Download file by id using fetch and blob (handles authenticated endpoints)
async function downloadFile(fileId, suggestedName) {
    try {
        const resp = await fetch(`/api/files/${fileId}`, {
            method: 'GET',
            credentials: 'same-origin'
        });
        if (!resp.ok) {
            alert('Ошибка при скачивании: ' + resp.status);
            return;
        }
        // Try to get filename from Content-Disposition if server provided it
        let filename = suggestedName || 'file';
        const disposition = resp.headers.get('Content-Disposition');
        if (disposition && disposition.includes('filename=')) {
            const match = disposition.match(/filename\*?=(?:UTF-8'')?["']?([^;"']+)["']?/i);
            if (match && match[1]) {
                filename = decodeURIComponent(match[1]);
            }
        }
        const blob = await resp.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(url);
    } catch (e) {
        console.error('downloadFile error', e);
        alert('Ошибка при скачивании файла');
    }
}

// DOM ready: bind UI handlers and auto-connect if nickname present
document.addEventListener('DOMContentLoaded', () => {
    initNicknameFromSession();

    // Buttons
    const connectBtn = document.getElementById('connectBtn');
    const sendBtn = document.getElementById('sendBtn');
    const disconnectBtn = document.getElementById('disconnectBtn');
    const uploadBtn = document.getElementById('uploadBtn');
    const fileInput = document.getElementById('fileInput');

    if (connectBtn) connectBtn.addEventListener('click', connect);
    if (sendBtn) sendBtn.addEventListener('click', sendMessage);
    if (disconnectBtn) disconnectBtn.addEventListener('click', disconnect);

    // Upload handler
    if (uploadBtn && fileInput) {
        uploadBtn.addEventListener('click', async () => {
            const file = fileInput.files[0];
            if (!file) return alert('Выберите файл');
            // Optional: basic validation
            const maxSize = 50 * 1024 * 1024; // 50 MB
            if (file.size > maxSize) return alert('Файл слишком большой (макс 50MB)');

            const form = new FormData();
            form.append('file', file);

            try {
                const resp = await fetch('/api/files', {
                    method: 'POST',
                    body: form,
                    credentials: 'same-origin'
                });
                if (!resp.ok) {
                    const text = await resp.text();
                    return alert('Upload failed: ' + text);
                }
                // Server will broadcast STOMP notification to all clients with file info
                const json = await resp.json().catch(() => null);
                if (json && json.fileId) {
                    console.log('Uploaded fileId', json.fileId);
                }
            } catch (e) {
                console.error('Upload error', e);
                alert('Ошибка при загрузке файла');
            }
        });
    }

    // Auto connect if nickname stored
    if (sessionStorage.getItem('chat_nickname')) {
        connect();
    }
});
