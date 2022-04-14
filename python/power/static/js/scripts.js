var layout = {
    font: {
      size: 10,
      color: 'white'
    },
    plot_bgcolor: '#212529',
    paper_bgcolor: '#212529',
    xaxis: {
        color: 'white'
    },
    yaxis: {
        color: 'white'
    }
}

Plotly.plot('power_over_day', null, layout);
Plotly.plot('power_over_month', null, layout);
Plotly.plot('power_over_year', null, layout);

$('#selected-date').click(function() {
    downloadAndUpdateChart($(this).text());
});

$('.date-selector a').click(function () {
    downloadAndUpdateChart($(this).text());
});

function downloadAndUpdateChart(selectedDate) {
    $.getJSON('pod/' + selectedDate).done(function(data) {
        Plotly.react('power_over_day', data["data"], $.extend(true, {}, layout));
    });
    $('#selected-date').text(selectedDate);
}

$( function() {
    // make it as accordion for smaller screens
    if (window.innerWidth < 992) {

        // close all inner dropdowns when parent is closed
        document.querySelectorAll('.navbar .dropdown').forEach(function(everyDropdown) {
            everyDropdown.addEventListener('hidden.bs.dropdown', function () {
                // after dropdown is hidden, then find all submenus
                this.querySelectorAll('.submenu').forEach(function(everySubmenu) {
                    // hide every submenu as well
                    everySubmenu.style.display = 'none';
                });
            })
        });

        document.querySelectorAll('.dropdown-menu a').forEach(function(element) {
            element.addEventListener('click', function (e) {
                let nextEl = this.nextElementSibling;
                if (nextEl && nextEl.classList.contains('submenu')) {
                    // prevent opening link if link needs to open dropdown
                    e.preventDefault();

                    // prevent the click even to be handled further
                    // which would result in an immediate closing of the submenu
                    e.stopPropagation();

                    if (nextEl.style.display == 'block') {
                        nextEl.style.display = 'none';
                    } else {
                        nextEl.style.display = 'block';
                    }
                }
            });
        })
    }

    $('.date-selector a')[0].click();
    $.getJSON('pom').done(function(data) {
        Plotly.react('power_over_month', data["data"], $.extend(true, {}, layout));
    });
    $.getJSON('poy').done(function(data) {
        Plotly.react('power_over_year', data["data"], $.extend(true, {}, layout));
    });
})