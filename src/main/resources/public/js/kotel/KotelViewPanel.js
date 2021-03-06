Ext.define('KotelViewPanel', {
    extend: 'Ext.panel.Panel',
    initComponent: function() {
        this.title = 'Наблюдение';
        this.border = true;
        this.frame = true;
        this.region = 'center';
        //this.height = 100;
        this.margins = '2 2 2 2';
        this.id = 'kotelViewPanel';
        this.collapsible = true;
        this.collapsed = false;
        this.resizable = false;
        this.bodyPadding = 10;
        this.bodyStyle = 'padding:10px;';
        baseW = 100;
        
        this.tp = 0;
        this.to = 0;
        this.t1 = 0;
        this.t2 = 0;
        this.t3 = 0;
        this.kw = 0;
        dtLock = true;
        this.destt = 0;
        
        this.destInit = false;
       
        
        this.initForm();

        KotelViewPanel.superclass.initComponent.apply(this, arguments);
    },
    initForm: function() {
        this.papa = this.initConfig().papa;
        
        this.task = { scope: this,
            run: function() { this.getValues() },
            interval: 5000
        };
        
        this.listeners = { scope: this,
            afterrender: function(){ 
                this.getValues();  
//                var m3 = document.getElementById("metka3");
//                m3.onmousedown = function(ev) {  
//                    dtLock = false;  return false;
//                };
//                m3.onmouseup = function(ev) {  
//                    dtLock = true;  return false;
//                };
////                m3.onmouseout = function(ev) {  
////                    dtLock = true;  
////                }
//                m3.onmousemove =this.changeDest;
                Ext.TaskManager.start(this.task);
            },
            resize: function(){ 
                this.resize();
            }
        };
        
        var tPatern = /^([0-9][0-9]).([0-9][0-9])$/i;
        
        this.html = '</br></br><div>Температура теплоносителя на выходе из котла</div>'+
                '</br><div><div class="prText" style="float:left">0°</div><div class="prText" style="float:right">100°</div></div></br>' +
                '<div class="shLine" id="metka1">55.6°<div></div></div>'+
                '<div class="shLine" id="metka3" style=":hover"><div></div>60.5°</div>'+
                '<div class="fillPr" id="gradusnik1"><div class="fillPrY" id="shkala1" style="width:100px"></div></div>'+
                '</br></br></br><div>Температура воды в накопителе</div>'+
                '</br><div><div class="prText" style="float:left">0°</div><div class="prText" style="float:right">100°</div></div></br>' +
                '<div class="shLine" id="metka2" style="top:145px;">60.44°<div></div></div>'+
                '<div class="fillPr" id="gradusnik2"><div class="fillPrG" id="shkala2" style="width:100px"></div></div>'+
                '</br></br></br><div>Температура воздуха на кухне</div>'+
                '</br><div><div class="prText" style="float:left">0°</div><div class="prText" style="float:right">100°</div></div></br>' +
                '<div class="shLine" id="metka4" style="top:242px;">19.44°<div></div></div>'+
                '<div class="fillPr" id="gradusnik3"><div class="fillPrG" id="shkala3" style="width:100px"></div></div>';
        
        this.tbar = [
            { text: ' Целевое значение: ', xtype: 'text'},
            Ext.create('Ext.form.field.Text', {
                id: 'destTempField', width: 40, validateOnChange: true
            }),
            Ext.create('Ext.Button', {text: 'сохранить', scope: this, disabled: false, id: 'saveButt',
                style: 'background-position: bottom center;', 
                 handler: function() {
                    this.setDest();
                 }
            })
        ];
    },
    resize: function() {
                
        var gr1 = getElementPosition("gradusnik1");
        baseW = gr1.width-20;
        
        var m1 = document.getElementById("metka1");
        m1.style.left = parseInt(baseW*this.tp/100 - 9) +'px';
        m1.innerHTML = this.tp.toFixed(2)+'°<div></div>';
        var sh1 = document.getElementById("shkala1");
        sh1.style.width = parseInt(baseW*this.tp/100) +'px';
        
        var m2 = document.getElementById("metka2");
        m2.style.left = parseInt(baseW*this.to/100 - 9) +'px';
        m2.innerHTML = this.to.toFixed(2)+'°<div></div>';
        var sh2 = document.getElementById("shkala2");
        sh2.style.width = parseInt(baseW*this.to/100) +'px';
        
        var m3 = document.getElementById("metka3");
        m3.style.left = parseInt(baseW*this.destt/100 - 9) +'px';
        m3.innerHTML = '<div></div>'+this.destt.toFixed(2)+'°';
        
        var m4 = document.getElementById("metka4");
        m4.style.left = parseInt(baseW*this.t1/100 - 9) +'px';
        m4.innerHTML = '<div></div>'+this.t1.toFixed(2)+'°';
        var sh3 = document.getElementById("shkala3");
        sh3.style.width = parseInt(baseW*this.t1/100) +'px';
        
    },
    getValues: function() {
        Ext.Ajax.request({
            url: '/api/getvalues', scope: this, method: 'GET',
            success: function(response, opts) {
              var ansv = Ext.decode(response.responseText);
              if(ansv.success) {  
                  this.tp = parseFloat(ansv.tp);
                  this.to = parseFloat(ansv.to);
                  this.t1 = parseFloat(ansv.t1);
                  this.t2 = parseFloat(ansv.t2);
                  this.t3 = parseFloat(ansv.t3);
                  this.destt = parseFloat(ansv.desttc);
                  
                  this.papa.kotelControlPanel.currVal[0]=parseInt(ansv.tp)+"";
                  this.papa.kotelControlPanel.currVal[1]=parseInt(ansv.to)+"";
                  this.papa.kotelControlPanel.currVal[2]=parseInt(ansv.kw)+"";  
                  if(!this.destInit) {
                    this.papa.kotelControlPanel.destVal[0]=parseInt(ansv.desttp)+"";
                    this.papa.kotelControlPanel.destVal[1]=parseInt(ansv.destto)+"";
                    this.papa.kotelControlPanel.destVal[2]=parseInt(ansv.destkw)+"";  
                    this.destInit = true;
                  }
                  this.papa.kotelControlPanel.dispCurrentView();
                  
                  if(Ext.getDom('ht1')) {
                        Ext.getDom('ht1').innerHTML = parseFloat(ansv.t1) +'°C</br></br>'+( (ansv.h1!='0.00') ? parseFloat(ansv.h1)+'%' : '');
                        Ext.getDom('ht2').innerHTML = parseFloat(ansv.t2) +'°C</br></br>'+parseFloat(ansv.h2)+'%';
                        Ext.getDom('ht3').innerHTML = parseFloat(ansv.t3) +'°C</br></br>'+parseFloat(ansv.h3)+'%';
                   }
                  this.resize();

              } else error_mes('Ошибка', 'ErrorCode:'+ansv.error.errorCode+"; "+ansv.error.errorMessage);  
            },
            failure: function() { }
        });
    },
    setDest: function() {
        
        this.destt = parseFloat(Ext.getCmp('destTempField').getValue());
        Ext.Ajax.request({
            url: '/api/setdestt', scope: this, method: 'GET',
            params: {desttc: this.destt.toFixed(2)},
            success: function(response, opts) {
              var ansv = Ext.decode(response.responseText);
              if(ansv.success) {  
                Ext.Msg.show({
                    title: 'Изменение целевой температуры',
                    msg: 'Целевая температура изменена. Текущее значение - '+ this.destt.toFixed(2),
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.INFO,
                    fn: null
                });

              } else error_mes('Ошибка', 'ErrorCode:'+ansv.error.errorCode+"; "+ansv.error.errorMessage);  
            },
            failure: function() { }
        });
    },
    changeDest: function(ev) {
        if(!dtLock) {
            var m3 = document.getElementById("metka3");
            var posX = parseInt(m3.style.left)+ev.movementX;
            this.destt = (100 * posX / baseW)+0.77;
            m3.innerHTML = '<div></div>'+this.destt.toFixed(2)+'°';
            m3.style.left = posX +'px';
        }
        return false;
    }

});