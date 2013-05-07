$(function() {
    $('#forums').empty();
        $.ajax({
        type: "GET",
        url: proxy+"type=xml&name=events",
        dataType: "xml",
        success: function(xml) {
            var current_date;
            var previous_date;
            $events = $(xml).find('events');
            $forums = $events.find('forums');
            $forums.find('schedule').each(function() {
                s_program_item_id = $(this).attr('mobile_item_id');
                s_id = $(this).attr('id');
                s_title = $(this).attr('title');
                s_start_time = $(this).attr('start_time');
                s_end_time = $(this).attr('end_time');
                s_venue = $(this).attr('venue');
                var sd = $.format.date(s_start_time, 'ddd, MMMM d');
                var st = $.format.date(s_start_time, 'hh:mm a');
                var et = $.format.date(s_end_time, 'hh:mm a');
                previous_date = current_date;
                current_date = sd;
               //to get the date to fill into localStorage
               var date = (s_start_time.split(' '))[0];
                 var checkedAttribute = false;
                 if (localStorage) {
                     value = localStorage.getItem(s_id);
                     if (value)
                         checkedAttribute = true;
                  } 

                if (previous_date != current_date)
                { 
                   $('<li data-role="list-divider"></li>').html(current_date).appendTo('#forums');
                }
                 
                $('<li id="'+s_id+'" data-role="fieldcontain"></li>').html('<fieldset data-role="controlgroup"><input type="checkbox" name="checkbox-'+s_id+'" id="checkbox-'+s_id+'" title="'+date+'" class="custom"/><a href="event_detail.html" id="program_'+s_program_item_id+'">&nbsp;&nbsp;'+s_title+'<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Time: '+st+' - '+et+'&nbsp;&nbsp;&nbsp;&nbsp;Venue: '+s_venue+'</a></fieldset>').appendTo('#forums');

                 $('#forums').listview('refresh');
                 $('#checkbox-'+s_id).attr('checked', checkedAttribute);	
            });
        }
    });

});

$('#forum-list-content').click(function(event) {
    event.target.setAttribute('clicked', 'yes');
    var clicked_item_id = $('a[clicked*="yes"]').attr('id');
    var item_id_split = clicked_item_id.split("_");
    var specific_id = item_id_split[1];
    
    $('#eventdetail').empty();

    $.ajax({
        type: "GET",
        url: proxy+'type=xml&name=items&id='+ specific_id,
        dataType: "xml",
        success: function(xml) {
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
                var date = (s_start_time.split(' '))[0];
                var checkedAttribute = false;
                if (localStorage) {
                     value = localStorage.getItem(s_id);
                     if (value)
                         checkedAttribute = true;
                }

                $('<li id="'+s_id+'" data-role="fieldcontain"></li>').html('<fieldset data-role="controlgroup"><input type="checkbox" name="checkbox-'+s_id+'" id="checkbox-'+s_id+'" title="'+date+'" class="custom"/><label for="checkbox-'+s_id+'">&nbsp;&nbsp;Date: ' + sd +'<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Time: '+st+' - '+et+'&nbsp;&nbsp;&nbsp;&nbsp;Venue: '+s_venue+'</label>').appendTo('#scheduleslist');
                $('#scheduleslist').listview('refresh');
                $('#checkbox-'+s_id).attr('checked', checkedAttribute);        
                $('#checkbox-'+s_id).click(function() {
		     var obj=new localstore();
                     var isChecked = $(this).is(':checked');
                    if (isChecked) {
                       checkbox_id = ($(this).attr('id')).split('-');
                       schedule_id = checkbox_id[1];
                       date = $(this).attr('title');
                         if (localStorage) {
                             value = obj.getvalue(schedule_id);
                             if (value)
                                 obj.add(schedule_id, date);
                             else
                                 localStorage[schedule_id] = date; 
                          } 
                    } else {
                         if (localStorage) {
                             value = obj.getvalue(schedule_id);
                             if(value)
                                 obj.remove(schedule_id);
                         }
                    }
                 });
             });
         }
    });
    var remove_clicked = document.getElementById(clicked_item_id);
    remove_clicked.removeAttribute('clicked');
});