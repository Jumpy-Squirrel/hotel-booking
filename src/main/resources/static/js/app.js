/*
 *    Copyright 2017 Eurofurence e.V.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

function disableClickOnNavbarLinks() {
    $('.nav li.noclick a').click(function() {
        return false;
    });
}

function activateLabel(label, active) {
    if (active) {
        $(label).addClass('active');
    } else {
        $(label).removeClass('active');
    }
}

function changedRoomsize(value) {
    activateLabel('#size_single_label', (value === '1'));
    activateLabel('#size_double_label', (value === '2'));
    activateLabel('#size_triple_label', (value === '3'));
    $('input.secondperson').prop('disabled',(value === '1'));
    $('input.thirdperson').prop('disabled',(value === '1' || value === '2'));
}

function switchActiveOnRoomsize() {
    $('input[type=radio][name=roomsize]').change(function() {
        changedRoomsize(this.value);
    });
}

function setInitialRoomsize() {
    var initialSize = $('#size_initial').val();
    if (initialSize === '3') {
        $('#size_triple').prop("checked", true);
    } else if (initialSize === '2') {
        $('#size_double').prop("checked", true);
    } else {
        $('#size_single').prop("checked", true);
    }
    changedRoomsize(initialSize);
}

$(document).ready(function() {
    disableClickOnNavbarLinks();
    switchActiveOnRoomsize();
    setInitialRoomsize();
});
