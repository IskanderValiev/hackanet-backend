'use strict';
document.querySelector('#welcomeForm').addEventListener('submit', connect, true);
document.querySelector('#dialogueForm').addEventListener('submit', sendMessage, true);
var stompClient = null;
var name = null;
var k = 0;

var headers = {
    login: 'mylogin',
    passcode: 'mypasscode',
    // additional header
    'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiQURNSU4iLCJlbWFpbCI6Imlza2FuZC52YWxpZXZAeWFuZGV4LnJ1Iiwic3ViIjoiMSJ9.XPbjyLPS_AHCtjbk9xEteRI_ruOtWSiCedR6O9HSKoKY1ZuXXdyfBDA2ere6diN4ice27ZG0w4WgX_1SmhQikg'
};
function connect(event) {
    name = document.querySelector('#name').value.trim();
    if (name) {
        document.querySelector('#welcome-page').classList.add('hidden');
        document.querySelector('#dialogue-page').classList.remove('hidden');
        var socket = new SockJS('http://localhost:8080/hackanet/ws');


        stompClient = Stomp.over(socket);
        stompClient.connect(headers, connectionSuccess);
    }
    console.log('connecting');
    event.preventDefault();
}

function onError() {
    console.log("Shit happens...")
}

function connectionSuccess() {
    k = 0;
    stompClient.send("/chat/3/connect", headers, "connected");
    stompClient.subscribe('/chat/3', onMessageReceived);
}

function sendMessage(event) {
    var messageContent = document.querySelector('#chatMessage').value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            text: document.querySelector('#chatMessage').value,
            sender_id: 2,
            chat_id: 2
        };
        stompClient.send("/chat/3/send", headers, JSON
            .stringify(chatMessage));
        document.querySelector('#chatMessage').value = '';
    }
    console.log('sending message');
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    console.log('message type: ' + message);
    document.querySelector("#typing").appendChild(typing.toString());
    if (k === 0) {
        for (var i = 0; i < message.length; i++) {
            messageElement.classList.add('message-data');
            // var element = document.createElement('i');
            // messageElement.appendChild(element);
            var usernameElement = document.createElement('span');
            var usernameText = document.createTextNode(message[i].sender.name);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
            var textElement = document.createElement('p');
            var messageText = document.createTextNode(message[i].text);
            textElement.appendChild(messageText);
            messageElement.appendChild(textElement);
            var imageElement = document.createElement('img');
            imageElement.setAttribute('src', message[i].attachments[0].preview_link);
            messageElement.appendChild(imageElement);
            document.querySelector('#messageList').appendChild(messageElement);
            document.querySelector('#messageList').scrollTop = document
                .querySelector('#messageList').scrollHeight;
        }
    } else {
        messageElement.classList.add('message-data');
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender.name);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.text);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
        document.querySelector('#messageList').appendChild(messageElement);
        document.querySelector('#messageList').scrollTop = document
            .querySelector('#messageList').scrollHeight;
    }
    k++;
}

//setup before functions
let typingTimer;                //timer identifier
let doneTypingInterval = 5000;  //time in ms (5 seconds)
let myInput = document.getElementById('chatMessage');
let sent = false;
let typing = false;

myInput.addEventListener('keydown', () => {
    typing = true;
    if (!sent) {
        var status = {
            user_id: 1,
            is_typing: true
        };
        stompClient.send("/chat/3/typing", headers, JSON.stringify(status));
    }
});


//on keyup, start the countdown
myInput.addEventListener('keyup', () => {
    clearTimeout(typingTimer);
    if (myInput.value) {
        typingTimer = setTimeout(doneTyping, doneTypingInterval);
    }
});

//user is "finished typing," do something
function doneTyping () {
    typing = false;
    //do something
    var status = {
        user_id: 1,
        is_typing: false
    };
    stompClient.send("/chat/3/typing", headers, JSON.stringify(status));
}