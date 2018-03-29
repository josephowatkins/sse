'use strict';

var eventSource = new EventSource('/stream');

eventSource.onmessage = function (e) {
    var data = JSON.parse(e.data);

    var element = document.createElement('li');
    var eventList = document.getElementById('list-js');

    element.innerHTML = `${data.timestamp}`;
    element.className = 'event-list__item';
    eventList.appendChild(element);
};

eventSource.onerror = function (e) {
    console.log(e);

    var element = document.createElement('li');
    var eventList = document.getElementById('list-js');

    element.innerHTML = '<b>disconected...</b>';
    element.className = 'event-list__item';
    eventList.appendChild(element);
};
