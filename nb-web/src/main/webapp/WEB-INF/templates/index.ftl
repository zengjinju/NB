<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,height=device-height">
    <title>多色散点图</title>
    <style>::-webkit-scrollbar{display:none;}html,body{overflow:hidden;height:100%;}</style>
</head>
<body>
<div id="mountNode"></div>
<input typ="hidden" value="${x}" id="x"/>
<input type="hidden" value="${y}" id="y" />
<script>/*Fixing iframe window.innerHeight 0 issue in Safari*/document.body.clientHeight;</script>
<script src="https://gw.alipayobjects.com/os/antv/assets/g2/3.0.0/g2.min.js"></script>
<script src="https://gw.alipayobjects.com/os/antv/assets/data-set/0.7.0/data-set.min.js"></script>
<script src="https://gw.alipayobjects.com/os/antv/assets/lib/jquery-3.2.1.min.js"></script>
<script>
    $.getJSON('/demo/data', {'x':$("#x").val(),'y':$('#y').val()},function(data){
        const chart = new G2.Chart({
            container: 'mountNode',
            forceFit: true,
            height: window.innerHeight
        });
        chart.source(data);
        chart.tooltip({
            showTitle: false,
            crosshairs: {
                type: 'cross'
            },
            itemTpl: '<li data-index={index} style="margin-bottom:4px;">'
            + '<span style="background-color:{color};" class="g2-tooltip-marker"></span>'
            + '{name}<br/>'
            + '{value}'
            + '</li>'
        });
        chart.point().position('height*weight')
                .color('gender')
                .size(4)
                .opacity(0.5)
                .shape('circle')
                .tooltip('gender*height*weight', (gender, height, weight) => {
            return {
                name: gender,
                value: height + '(cm), ' + weight + '(kg)'
            };
    });
        chart.render();
    });
</script>
</body>
</html>
