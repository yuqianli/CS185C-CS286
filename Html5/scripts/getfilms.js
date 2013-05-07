/**
 * author: Snigdha Mokkapati
 */

function goBack() {
    history.go(-1);
}

$(function() {

    var clicked_list;
    var specific_film_url;

    function get_short_film_detail(id) {
        clicked_list = 'title_list';
        specific_film_url = proxy+"type=film&id="+id;
         
        $(get_film_detail); 
    }
    
    function get_film_detail() {
      $('#filmdetail').empty();
      $.ajax({
         type: "GET",
         url: specific_film_url,
         dataType: "xml",
         success: function(xml) {
             if (clicked_list == 'date_list') {
                 $program_item = $(xml).find('program_item');
                 $schedules = $program_item.find('schedules');
                 $film = $program_item.find('film');
                 var titles = $program_item.find('title');
                 var descriptions = $program_item.find('description');
                 var images = $program_item.find('imageURL');
                 var program_title = titles.first().text();
                 var program_description = descriptions.first().text();
                 var program_image = images.first().text();
                 $('<div id="content-title" align="center"></div>').html('<h3>'+program_title+'</h3>').appendTo('#filmdetail');
                 $('<br>').appendTo('#filmdetail');
                 $('<div style="text-align: center;"></div>').html('<img src="'+program_image+'" alt="Alternate image"/>').appendTo('#filmdetail');
                 $('<br>').appendTo('#filmdetail');
                 if($film.size() == 1) {
                     var title = $film.find('title').text();
                     var description = $film.find('description').text();
                     var genre = $film.find('genre').text();
                     var cinematographer = $film.find('cinematographer').text();
                     var director = $film.find('director').text();
                     var writer = $film.find('writer').text();
                     var language = $film.find('language').text();
                     var editor = $film.find('editor').text();
                     var cast = $film.find('cast').text();
                     var producer = $film.find('producer').text();
                     var country = $film.find('country').text();
                     var image = $film.find('imageURL').text();
                 
                     $('<div id="content-description"></div>').html(description).appendTo('#filmdetail');
                     $('<br><b>Genre:</b> '+genre+'<br><b>Cinematographer:</b> '+cinematographer+'<br><b>Director:</b> '+director+'<br><b>Writer:</b> '+writer+'<br><b>Language:</b> '+language+'<br><b>Editor:</b> '+editor+'<br><b>Cast:</b> '+cast+'<br><b>Producer:</b> '+producer+'<br><b>Country:</b> '+country+'<br><br>').appendTo('#filmdetail');
                  } else if ($film.size() > 1) {
                     $('<div id="content-description"></div>').html(program_description).appendTo('#filmdetail');
                     $('<br>').appendTo('#filmdetail');
                     $('<b> Films included in this short program:</b><br>').appendTo('#filmdetail');
                      $('<div id="short-film-list"></div>').appendTo('#filmdetail');
                      $('#short-film-list').html('<ul data-role="listview" id="short_films" data-inset="true"></ul>');
                      $('#short-film-list').trigger('create');
                   
                     $film.each(function() {
                         f_id = $(this).attr('id');
                         f_title = $(this).find('title').text();
                         $('<li></li>').html('<fieldset data-role="controlgroup"><a href="#" id="a_'+f_id+'" data-source="title">'+f_title+'</a></fieldset>').appendTo('#short_films');
                         //to bind a jquery function that takes parameters
			 var param = f_id;
			 $('#a_'+f_id).click(function(){
			     get_short_film_detail(param); 
                         });
                     	    
                      $('#short_films').listview('refresh');
                     });    
                  }
             } else {
                  $film = $(xml).find('film');
                  $schedules = $(xml).find('schedules');
                  var title = $film.find('title').text();
                  var description = $film.find('description').text();
                  var genre = $film.find('genre').text();
                  var cinematographer = $film.find('cinematographer').text();
                  var director = $film.find('director').text();
                  var writer = $film.find('writer').text();
                  var language = $film.find('language').text();
                  var editor = $film.find('editor').text();
                  var cast = $film.find('cast').text();
                  var producer = $film.find('producer').text();
                  var country = $film.find('country').text();
                  var image = $film.find('imageURL').text();
                  $('<div id="content-title" align="center"></div>').html('<h3>'+title+'</h3>').appendTo('#filmdetail');
                  $('<div style="text-align: center;"></div>').html('<img src="'+image+'" alt="Alternate image"/>').appendTo('#filmdetail');
                  $('<br>').appendTo('#filmdetail');
                  $('<div id="content-description"></div>').html(description).appendTo('#filmdetail');
                  $('<br><b>Genre:</b> '+genre+'<br><b>Cinematographer:</b> '+cinematographer+'<br><b>Director:</b> '+director+'<br><b>Writer:</b> '+writer+'<br><b>Language:</b> '+language+'<br><b>Editor:</b> '+editor+'<br><b>Cast:</b> '+cast+'<br><b>Producer:</b> '+producer+'<br><b>Country:</b> '+country+'<br><br>').appendTo('#filmdetail');
             }

             //Schedule part
             $('<div id="schedule-content"></div>').appendTo('#filmdetail');
             $('#schedule-content').html($('<ul data-role="listview" id="scheduleslist" data-inset="true"></ul>'));
             $('#schedule-content').trigger('create');
             $('#scheduleslist').append($('<li data-role="list-divider">Schedules</li>'));
             $('#scheduleslist').listview('refresh');

             $schedules.find('schedule').each(function() {
                 s_id = $(this).attr('id');
               	 //s_program_item_id = $(this).attr('program_item_id');
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
                 // Where checkbox clicking function to add to localStorage has to be added. id of checkbox is checkbox-id
                 
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
    }

$('.list-content').click(function(event) {
    event.target.setAttribute('clicked', 'yes');
    var clicked_item_id = $('a[clicked*="yes"]').attr('id');
    clicked_source = $('a[clicked*="yes"]').attr('data-source');
    var item_id_split = clicked_item_id.split("_");
    var specific_id;    

    if (item_id_split[0] == 'program') {
        clicked_list = 'date_list';
        specific_id = item_id_split[1];
        specific_film_url = proxy+"type=program_item&id="+specific_id;
    } else {
        clicked_list = 'title_list';
        specific_id = clicked_item_id;
        specific_film_url = proxy+"type=film&id="+specific_id;
    }

    $(get_film_detail);
    
    var remove_clicked = document.getElementById(clicked_item_id);
    remove_clicked.removeAttribute('clicked');
});

  
  source_list_for_header = "date_list";


  function extract_films_by_title() {
    $('#date_header').hide();
    source_list_for_header = "title_list";
    $('#films').empty();
    $.ajax({
        type: "GET",
        url: proxy+"type=films",
        dataType: "xml",
        success: function(xml) { 
            $films = $(xml).find('films');
            var current_letter;
            var previous_letter;
            $films.find('film').each(function(){
                id = $(this).attr('id');
                title = $(this).find('title').text();
                var split = title.split(" ");
                if (split[0] == "The" || split[0] == "A") {
                    previous_letter = current_letter;
                    current_letter = split[1].charAt(0);
                } else {
                    previous_letter = current_letter;
                    current_letter = split[0].charAt(0);
                }
                if (previous_letter != current_letter) 
                    $('<li class="film_list" title="'+current_letter+'" data-role="list-divider" style="height:20px;"></li>').html(current_letter).appendTo('#films');
                
                $('<li id="'+id+'" class="film_list" title="'+current_letter+'" data-role="fieldcontain" style="height:20px;"></li>').html('<fieldset data-role="controlgroup"><a href="film_detail.html" id="'+id+'" data-source="title">&nbsp;&nbsp;'+title+'</a></fieldset>').appendTo('#films');
                
                $('#films').listview('refresh');
            });
         }
    });
}


function extract_films_by_date() {
    $('#date_header').hide();
    source_list_for_header = "date_list";
    $('#films').empty();
    $.ajax({
        type: "GET",
        url: proxy+"type=schedules",
        dataType: "xml",
        success: function(xml) {
            $schedules = $(xml).find('schedules');
            var current_date;
            var previous_date;
            $schedules.find('schedule').each(function(){
                 s_id = $(this).attr('id');
                 s_program_item_id = $(this).attr('program_item_id');
                 s_title = $(this).attr('title');
                 s_start_time = $(this).attr('start_time');
                 s_end_time = $(this).attr('end_time');
                 s_venue = $(this).attr('venue');
                 var sd = $.format.date(s_start_time, 'ddd, MMMM d');
                 var st = $.format.date(s_start_time, 'hh:mm a');
                 var et = $.format.date(s_end_time, 'hh:mm a');
                 previous_date = current_date;
                 current_date = sd;
                // to get the date to fill into localstorage
                 var date = (s_start_time.split(' '))[0];
                 var checkedAttribute = false;
                 if (localStorage) {
                     value = localStorage.getItem(s_id);
                     if (value)
                         checkedAttribute = true;
                 }

                if(previous_date != current_date)
                    $('<li class="film_list" data-role="list-divider" title="'+current_date+'" style="line-height:2em; height:35px;"></li>').html(current_date).appendTo('#films');              
                
                $('<li id="'+s_id+'" class="film_list" title="'+current_date+'" data-role="fieldcontain" style="height:35px;"></li>').html('<fieldset data-role="controlgroup"><input type="checkbox" name="checkbox-'+s_id+'" id="checkbox-'+s_id+'" title="'+date+'" class="custom"/><a href="film_detail.html" id="program_'+s_program_item_id+'" data-source="date">&nbsp;&nbsp;'+s_title+'<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Time: '+st+' - '+et+'&nbsp;&nbsp;&nbsp;&nbsp;Venue: '+s_venue+'</a></fieldset>').appendTo('#films');

                $('#films').listview('refresh');
                $('#checkbox-'+s_id).attr('checked', checkedAttribute);
               
            // Where checkbox clicking function to add to localStorage has to be added. 
                $('#checkbox-'+s_id).change(function() {
                     var obj=new localstore();
                     var isChecked = $(this).is(':checked');
                    if (isChecked) {
                       checkbox_id = ($(this).attr('id')).split('-');
                       schedule_id = checkbox_id[1];
                       date = $(this).attr('title');
                       if (localStorage) {
                             value = localStorage.getItem(schedule_id);
                             if (value){
                                 obj.add(schedule_id, date);
                             }
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
}
    
    $('#title-btn').click(extract_films_by_title);
    $('#date-btn').click(extract_films_by_date);
    
    if(typeof clicked_source == 'undefined')
	$(extract_films_by_date);
    else if(clicked_source == 'title') {
        $(extract_films_by_title);
    } else {
	$(extract_films_by_date);
    }
    $("#filmstag").attr("class", "ui-btn-active");
});