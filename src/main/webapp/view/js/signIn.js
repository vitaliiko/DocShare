//
//$('.btn').click(function() {
//    var login = $('#login').val();
//    var password = $('#password').val();
//    $.ajax({
//        url: '/main/sign_in',
//        type: 'post',
//        data: {login: login, password: password},
//        success: function() {
//            alert('fuck');
//            $('#danger-div').html(
//                "<div class='alert alert-danger'>" +
//                "<a href='#' class='close' data-dismiss='alert'>&times;</a>" +
//                "<strong>Error!</strong> Wrong login or password" +
//                "</div>"
//            );
//        },
//        error: function() {
//            $('#danger-div').html(
//                "<div class='alert alert-danger'>" +
//                    "<a href='#' class='close' data-dismiss='alert'>&times;</a>" +
//                    "<strong>Error!</strong> Wrong login or password" +
//                "</div>"
//            );
//        }
//    })
//});