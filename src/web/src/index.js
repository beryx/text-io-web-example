require('./schedule.css');
var Flatpickr = require('flatpickr');
require('flatpickr/dist/flatpickr.css');
require('flatpickr/dist/themes/material_blue.css');
var pickerElem = document.getElementById('datepicker');
var picker = new Flatpickr(pickerElem, {defaultDate: new Date(), inline: true});
var Table = require('table-builder');

var butRun = document.getElementById('butRun');

require('text-io/textterm.css');
var createTextTerm = require('text-io');
var textTerm = createTextTerm(document.getElementById('textterm'));
textTerm.onDispose = function(resultData) {
    document.getElementById('title').hidden = true;
    appInElem.hidden = true;
    appTermElem.hidden = true;
    appOutElem.hidden = false;

    var res = JSON.parse(resultData);
    document.querySelector('.schedule-title').textContent = res.title;

    var schedule = document.querySelector('.schedule-table');
    schedule.innerHTML = new Table({'class': 'schedule-table'})
        .setHeaders(res.tableHeaders)
        .setData(res.tableData)
        .render();
};
textTerm.onAbort = function() {
    document.getElementById('app-done').textContent = 'Program aborted by the user. You can now close this window.';
};

var appInElem = document.getElementById('app-in');
var appTermElem = document.getElementById('app-term');
var appOutElem = document.getElementById('app-out');

butRun.onclick = function() {
    appInElem.hidden = true;
    appTermElem.hidden = false;
    var date = picker.selectedDates[0];
    var initData = {
        year: date.getYear(),
        month: date.getMonth(),
        day: date.getDate()
    };
    textTerm.execute(initData);
};
