Ext.define('MapPanel', {
    extend: 'Ext.panel.Panel',
    initComponent: function() {
        this.title = 'Карта';
        this.border = true;
        this.frame = true;
        this.region = 'center';
        //this.height = 100;
        this.margins = '2 2 2 2';
        this.id = 'controlPanel';
        this.collapsible = true;
        this.collapsed = false;
        this.resizable = false;
        this.autoScroll = true;
        this.bodyPadding = 10;
        this.bodyStyle = 'padding:10px; background: #ffffff'; //#cbddf3;';
        
        this.initForm();

        MapPanel.superclass.initComponent.apply(this, arguments);
    },
    initForm: function() {
        this.papa = this.initConfig().papa;
        this.html = '<div id="trmap" ></div>';
        this.listeners = { scope: this,
            render: function() {
                this.resizeImage();
            },
            resize: function() {
                this.resizeImage();
            }
        };
        
        this.devList = Ext.create('Ext.form.ComboBox', {
            fieldLabel: 'Устройство', id: 'devicesList',
            store: devStrore,
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id'
        });
        
        this.tbar = [
            this.devList, '-',
            Ext.create('Ext.Button', {text: 'Get', scope: this, disabled: false, id: 'execButt',
                style: 'background-position: bottom center;', 
                 handler: function() { this.getTrac(); }
            }),
            '->', '-',
            Ext.create('Ext.Button', {text: 'Clear', scope: this, disabled: false, id: 'clearButt',
                style: 'background-position: bottom center;', 
                handler: function() { this.clearMap(); }
            })
        ];
    },
    resizeImage: function() {
        var h = this.getHeight();
        var w = this.getWidth();
        var winRatio = w/h;
        var imgW = w - 30;       
        Ext.getDom('trmap').style.width= imgW+'px';
        var imgH = h - 60;
        Ext.getDom('trmap').style.height=imgH+'px';
    },
    mapInit: function(data) {
        // Создаем карту.
        var myMap = new ymaps.Map("trmap", {
            center: [55.663973, 37.553158],
            zoom: 16
        }, {
            searchControlProvider: 'yandex#search'
        });
        var myGeoObject = new ymaps.GeoObject({
            // Описываем геометрию геообъекта.
            geometry: {
                // Тип геометрии - "Ломаная линия".
                type: "LineString",
                // Указываем координаты вершин ломаной.
                coordinates: [
                    [55.663753, 37.553439],
                    [55.664914, 37.554943]
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
        var myPolyline = new ymaps.Polyline(data, {
            // Описываем свойства геообъекта.
            // Содержимое балуна.
            balloonContent: "Ломаная линия"
        }, {
            // Задаем опции геообъекта.
            // Отключаем кнопку закрытия балуна.
            balloonCloseButton: false,
            // Цвет линии.
            strokeColor: "#FF0000",
            // Ширина линии.
            strokeWidth: 2,
            // Коэффициент прозрачности.
            strokeOpacity: 0.8
        });

        // Добавляем линии на карту.
        myMap.geoObjects
            //.add(myGeoObject)
            .add(myPolyline);
    },
    getTrac: function() {
        this.clearMap();
        
        var devId = this.devList.getValue();
        
        Ext.Ajax.request({
            url: '/api/gettrdata', scope: this, method: 'GET',
            params: {deviceid: devId},
            success: function(response, opts) {
              var ansv = Ext.decode(response.responseText);
              if(ansv.success) { 
                var data = ansv.data;
                this.mapInit(data);
              } else 
                error_mes('Ошибка', 'ErrorCode:'+ansv.error.errorCode+"; "+ansv.error.errorMessage);  
            },
            failure: function() { }
        });
       
    },
    clearMap: function() {
        Ext.getDom('trmap').innerHTML= '';
    }
});