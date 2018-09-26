<%-- 
    Document   : main
    Created on : 24.08.2017, 13:45:13
    Author     : k.baukov
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <link rel="SHORTCUT ICON" href="img/favicon.ico">
        <!--link href="/extjs/resources/ext-theme-classic/ext-theme-classic-all.css" rel="stylesheet" type="text/css">
        <link href="/extjs/resources/ext-theme-neptune/ext-theme-neptune-all.css" rel="stylesheet" type="text/css"-->
        <link href="/extjs/resources/ext-theme-gray/ext-theme-gray-all.css" rel="stylesheet" type="text/css">
        <link href="/css/customStyle.css" rel="stylesheet" type="text/css">
        <script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
        <script type="text/javascript" src="/extjs/ext-all-debug.js"></script>
        <script type="text/javascript" src="/extjs/locale/ext-lang-ru.js"></script>        
        <script type="text/javascript" src="/js/common.js"></script>
        <script type="text/javascript" src="/js/main/main.js"></script>
        <script type="text/javascript" src="/js/main/MapPanel.js"></script>
        <script type="text/javascript" src="/js/main/KotelTabPanel.js"></script>
        <script type="text/javascript" src="/js/kotel/KotelViewPanel.js"></script>
        <script type="text/javascript" src="/js/kotel/KotelControlPanel.js"></script>
        <script type="text/javascript" src="/js/main/SettingsTab.js"></script>
        <script type="text/javascript" src="/js/main/Devices.js"></script>
        <script type="text/javascript" src="/js/main/Users.js"></script>
        <script type="text/javascript" src="/js/main/ControlTab.js"></script>
        <script type="text/javascript">var user = ${uName};</script>
        <!--script type="text/javascript">
        ymaps.ready(init);
      
        init = function() {
    // Создаем карту.
    var myMap = new ymaps.Map("map", {
            center: [55.663973, 37.553158],
            zoom: 5
        }, {
            searchControlProvider: 'yandex#search'
        });

    // Создаем ломаную, используя класс GeoObject.
    var myGeoObject = new ymaps.GeoObject({
            // Описываем геометрию геообъекта.
            geometry: {
                // Тип геометрии - "Ломаная линия".
                type: "LineString",
                // Указываем координаты вершин ломаной.
                coordinates: [
                    [55.80, 37.50],
                    [55.70, 37.40]
                ]
            },
            // Описываем свойства геообъекта.
            properties:{
                // Содержимое хинта.
                hintContent: "Я геообъект",
                // Содержимое балуна.
                balloonContent: "Меня можно перетащить"
            }
        }, {
            // Задаем опции геообъекта.
            // Включаем возможность перетаскивания ломаной.
            draggable: true,
            // Цвет линии.
            strokeColor: "#FF6600",
            // Ширина линии.
            strokeWidth: 1
        });

    // Создаем ломаную с помощью вспомогательного класса Polyline.
    var myPolyline = new ymaps.Polyline([
            // Указываем координаты вершин ломаной.
            [55.80, 37.50],
            [55.80, 37.40],
            [55.70, 37.50],
            [55.70, 37.40]
        ], {
            // Описываем свойства геообъекта.
            // Содержимое балуна.
            balloonContent: "Ломаная линия"
        }, {
            // Задаем опции геообъекта.
            // Отключаем кнопку закрытия балуна.
            balloonCloseButton: false,
            // Цвет линии.
            strokeColor: "#000000",
            // Ширина линии.
            strokeWidth: 1,
            // Коэффициент прозрачности.
            strokeOpacity: 0.5
        });

    // Добавляем линии на карту.
    myMap.geoObjects
        .add(myGeoObject)
        .add(myPolyline);
}
    </script-->
        
        <title>BM</title>
    </head>    
    <body id="boby"><div id="vp"></div></body>
</html>
