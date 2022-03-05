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
Plotly.plot('power_bar', null, layout);
Plotly.plot('power_over_month', null, layout);

$('.dropdown-menu a').click(function () { 
    var selectedDate = $(this).text();
    $.getJSON('pod/' + selectedDate).done(function(data) {
        data["data"][0]["name"] = "House and pool";
        data["data"][1]["name"] = "Heat pump";
        Plotly.react('power_over_day', data["data"], $.extend(true, {}, layout));
    });
    $('#selected-date').text(selectedDate);
});

$( function() {
    $('.dropdown-menu a')[0].click();
    $.getJSON('pb').done(function(data) {
        Plotly.react('power_bar', data["data"], $.extend(true, {}, layout));
    });
    $.getJSON('pom').done(function(data) {
        Plotly.react('power_over_month', data["data"], $.extend(true, {}, layout));
    });
})