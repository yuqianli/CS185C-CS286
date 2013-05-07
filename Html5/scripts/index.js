$(function() {
     $.ajax({
        type: "GET",
        url: proxy+"type=xml&name=ihome",
        dataType: "xml",
        success: function(xml) {
            $mobileAppHome = $(xml).find('mobileAppHome');
            $mobileAppHome.find('section').each(function() {
                name = $(this).attr('name');
                if (name == 'Header') {
                    $('#image-content').empty();
                    $item = $(this).find('item');
                    var image_url = $item.find('imageURL').text();
                    $('<div style="text-align: center;"></div>').html('<img src="'+image_url+'" alt="home image"/>').appendTo('#image-content');
                }
                else if (name == 'Top News') { // Top News Section
                    $('#news').empty();
                    $('<li data-role="list-divider"></li>').html('Top News').appendTo('#news');
                    $('#news').listview('refresh');
                    $items = $(this).find('item');
                    $items.each(function() {
                        i_title = $(this).find('title').text();
                        //i_date = $(this).find('date').text();
                        //i_link_type = $(this).find('link').attr('type');
                        i_link_id = $(this).find('link').attr('id');
                        //i_imageURL = $(this).find('imageURL').text();
                        $('<li></li>').html('<a href="event_detail.html" id="item_'+i_link_id+'">'+i_title+'</a>').appendTo('#news');
                        $('#news').listview('refresh');
                    });
                }
                else if (name == 'Cinequest News') { //Cinequest News Section
                     
                     $('#news').append($('<li data-role="list-divider">Cinequest News</li>'));
                     $('#news').listview('refresh');
                     $items = $(this).find('item');
                     $items.each(function() {
                         i_title = $(this).find('title').text();
                         i_link_id = $(this).find('link').attr('id');
                         $('<li></li>').html('<a href="event_detail.html" id="item_'+i_link_id+'">'+i_title+'</a>').appendTo('#news');
                         $('#news').listview('refresh');
                     });
                 }
            });
          }
     });
});

$('#news-content').click(function(event) {
   //event.stopImmediatePropagation();
   //event.stopPropogation();
   event.target.setAttribute('clicked', 'yes');
    var clicked_item_id = $('a[clicked*="yes"]').attr('id');
    var item_id_split = clicked_item_id.split("_");
    var specific_id = item_id_split[1];
    //alert(specific_id);
    $.ajax({
        type: "GET",
        url: proxy+'type=xml&name=items&id='+ specific_id,
        dataType: "xml",
        success: function(xml) {
            //ajax script was being called twice .. stopImmediatePropagation did not help, but this works.
            $('#eventdetail').empty();
            $program_item = $(xml).find('program_item');
            var title = $program_item.find('title').text();
            var description = $program_item.find('description').text();
            $schedules = $program_item.find('schedules');
            $('<div id="content-title" align="center"></div>').html('<h3>'+title+'</h3>').appendTo('#eventdetail');
            $('<div id="content-description"></div>').html(description).appendTo('#eventdetail');
      
            //Schedule part
            $('<div id="schedule-content"></div>').appendTo('#eventdetail');
            $('#schedule-content').html($('<ul data-role="listview" id="scheduleslist" data-inset="true"></ul>'));
            $('#schedule-content').trigger('create');
            $('#scheduleslist').append($('<li data-role="list-divider">Schedules</li>'));
            $('#scheduleslist').listview('refresh');
            
            $schedules.find('schedule').each(function() {
                s_id = $(this).attr('id');
                s_start_time = $(this).attr('start_time');
                s_end_time = $(this).attr('end_time');
                s_venue = $(this).attr('venue');
                var sd = $.format.date(s_start_time, 'ddd, MMMM d');
                var st = $.format.date(s_start_time, 'hh:mm a');
                var et = $.format.date(s_end_time, 'hh:mm a');

                $('<li id="'+s_id+'" data-role="fieldcontain"></li>').html('<fieldset data-role="controlgroup"><input type="checkbox" name="checkbox-'+s_id+'" id="checkbox-'+s_id+'" class="custom"/><label for="checkbox-'+s_id+'">&nbsp;&nbsp;Date: ' + sd +'<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Time: '+st+' - '+et+'&nbsp;&nbsp;&nbsp;&nbsp;Venue: '+s_venue+'</label>').appendTo('#scheduleslist');
                $('#scheduleslist').listview('refresh'); 
            });
         }
    });
    var remove_clicked = document.getElementById(clicked_item_id);
    remove_clicked.removeAttribute('clicked');

});