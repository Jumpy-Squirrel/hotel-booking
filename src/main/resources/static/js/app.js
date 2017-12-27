function isMainPage() {
    return $('#mainpage').length > 0;
}

function unhideInfoWell() {
    $('#unhide_with_js').css('visibility', 'visible');
    $('#hide_with_js').css('visibility', 'hidden');
}

function isFormPage() {
    return $('#formpage').length > 0;
}

function disableClickOnNavbarLinks() {
    $('.nav li.noclick a').click(function() {
        return false;
    });
}

function updatePrices() {
    var roomsize =  $('input[name=roomsize]:checked').val();
    var maxType = $('#roomtype_max').val();
    for (var i = 1; i <= maxType; i++) {
        var price = $('#price'+i+'_'+roomsize).val() + '*';
        $('#roomtype'+i+'price').text(price);
    }
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
    updatePrices();
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

function changedRoomtype(value) {
    var maxType = $('#roomtype_max').val();
    for (var i = 1; i <= maxType; i++) {
        activateLabel('#roomtype'+i+'button', i == value);
    }
}

function switchActiveOnRoomtype() {
    $('input[type=radio][name=roomtype]').change(function() {
         changedRoomtype(this.value);
    });
}

function setInitialRoomtype() {
    var initialType = $('#roomtype_initial').val();
    $('#roomtype'+initialType).prop("checked", true);
    changedRoomtype(initialType);
}

function initializeDatepicker() {
    var datepickerLanguage = $('#languagecode').attr('class');
    if (datepickerLanguage === 'en') {
        datepickerLanguage = 'en-GB';
    }
    var dateFormat = $('#dateformat').val();
    var d1s = $('#date1s').val();
    var d1e = $('#date1e').val();
    var d2s = $('#date2s').val();
    var d2e = $('#date2e').val();
    $('#arrival').datepicker({
        language: datepickerLanguage,
        format: dateFormat,
        startDate: d1s,
        endDate: d1e
    });
    $('#departure').datepicker({
        language: datepickerLanguage,
        format: dateFormat,
        startDate: d2s,
        endDate: d2e
    });
}

function canSubmit() {
    return $('input[type=checkbox][name=understood]:checked').length;
}

function preventSubmitUntilConfirmed() {
    $("#form").submit(function(e) {
        if(!canSubmit()) {
            alert("Please confirm that you have understood what you will need to do after clicking 'generate email'!");

            //stop the form from submitting
            return false;
        }

        return true;
    });
}

function changedConfirm() {
    var submitbutton = $('#submitbutton');
    if (canSubmit()) {
        submitbutton.removeClass('btn-default');
        submitbutton.addClass('btn-primary');
        submitbutton.addClass('active');
    } else {
        submitbutton.removeClass('active');
        submitbutton.removeClass('btn-primary');
        submitbutton.addClass('btn-default');
    }
}

function switchSubmitActiveOnConfirm() {
    $('input[type=checkbox][name=understood]').change(function() {
        changedConfirm();
    });
}

$(document).ready(function() {
    disableClickOnNavbarLinks();

    if (isMainPage()) {
        unhideInfoWell();
    }
    if (isFormPage()) {
        preventSubmitUntilConfirmed();

        switchActiveOnRoomsize();
        setInitialRoomsize();

        initializeDatepicker();

        switchActiveOnRoomtype();
        setInitialRoomtype();

        updatePrices();

        switchSubmitActiveOnConfirm();
    }
});
