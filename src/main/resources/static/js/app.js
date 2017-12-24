function disableClickOnNavbarLinks() {
    $(".nav li.noclick a").click(function() {
        return false;
    });
}

$(document).ready(function() {
    disableClickOnNavbarLinks();
});
