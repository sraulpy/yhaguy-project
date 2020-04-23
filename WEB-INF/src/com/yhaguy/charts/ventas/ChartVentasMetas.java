package com.yhaguy.charts.ventas;

import java.util.Date;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Color;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.yhaguy.domain.HistoricoVentaVendedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;



@SuppressWarnings("serial")
public class ChartVentasMetas extends SelectorComposer<Window> {

    @Wire
    Charts chart;
    
    @Wire
    Label lbl_dia;
    
    @Wire
    Label lbl_dia_;
    
    @Wire
    Label lbl_mob;
    
    @Wire
    Label lbl_vtas_mes;
    
    @Wire
    Label lbl_cob_mes;
    
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        
        AccesoDTO acc = (AccesoDTO) Sessions.getCurrent().getAttribute("AccesoDTO");
        long idSuc = 2;
        if (acc != null) {
        	idSuc = acc.getSucursalOperativa().getId();
		}
        
        Double ventas = new Double(0);
        Double cobranzas = new Double(0);
        Double meta = new Double(0);
        Double dia = new Double(0);
        Double c_dia = new Double(0);
        Date hoy = new Date();
        Date inicioMes = Utiles.getFechaInicioMes();
        
        RegisterDomain rr = RegisterDomain.getInstance();
        int anho = Integer.parseInt(Utiles.getAnhoActual());
		int mes = Integer.parseInt(Utiles.getMesActual());
        HistoricoVentaVendedor hist = rr.getHistoricoVentaVendedor(anho, mes, 0, idSuc);        
        
        double diaVtas = (double) rr.getTotalVentas(hoy, hoy)[0];
    	double diaNcs = (double) rr.getTotalNotasCredito(hoy, hoy)[0];
    	double diaIva = Utiles.getIVA((diaVtas - diaNcs), 10);
        dia = (diaVtas - diaNcs) - diaIva;
        
        double diaCob = (double) rr.getTotalCobranzas(hoy, hoy)[0];
        double diaVtasCon = (double) rr.getTotalVentasContado(hoy, hoy)[0];
    	double diaNcsCon = (double) rr.getTotalNotasCreditoContado(hoy, hoy)[0];
        double diaCobIva = Utiles.getIVA(diaCob + (diaVtasCon - diaNcsCon), 10);
        c_dia = (diaCob + (diaVtasCon - diaNcsCon)) - diaCobIva;
        
        if (hist != null) {
        	double vtas = (double) rr.getTotalVentas(inicioMes, hoy)[0];
        	double ncs = (double) rr.getTotalNotasCredito(inicioMes, hoy)[0];
        	double iva = Utiles.getIVA((vtas - ncs), 10);
        	double cob = (double) rr.getTotalCobranzas(inicioMes, hoy)[0];
        	double vtasCon = (double) rr.getTotalVentasContado(inicioMes, hoy)[0];
        	double ncsCon = (double) rr.getTotalNotasCreditoContado(inicioMes, hoy)[0];
            double cobIva = Utiles.getIVA(cob + (vtasCon - ncsCon), 10);
			ventas = (vtas - ncs) - iva;
			cobranzas = (cob + (vtasCon - ncsCon)) - cobIva;
			meta = hist.getMeta();
		}
        
        CategoryModel model = new DefaultCategoryModel();
        
        model.setValue("Cobranzas", "Cobranzas / Ventas", cobranzas.longValue());
        model.setValue("Ventas", "Cobranzas / Ventas", ventas.longValue());
        model.setValue("Meta " + (lbl_mob != null? "Gs. " + Utiles.getNumberFormat(meta) : "") , "Cobranzas / Ventas", meta.longValue());
        
        chart.setModel(model);
        
        chart.getCredits().setEnabled(false);
        
        lbl_dia.setValue("Cobranzas del Día: " + Utiles.getNumberFormat(c_dia));
        lbl_dia_.setValue("Ventas del Día: " + Utiles.getNumberFormat(dia));
        
        chart.getSeries().setColor(new Color("#357ebd"));
        
        if (lbl_mob != null) {
        	chart.getTitle().setStyle("font-size: 25pt");
        	chart.getLabels().setStyle("font-size: 24pt");
        	chart.getLegend().setItemStyle("font-size: 24pt");
        	chart.getXAxis().getLabels().setStyle("font-size: 14pt");
        	chart.getYAxis().getLabels().setStyle("font-size: 14pt");
        	lbl_vtas_mes.setValue("Ventas del Mes: " + Utiles.getNumberFormat(ventas));
            lbl_cob_mes.setValue("Cobranzas del Mes: " + Utiles.getNumberFormat(cobranzas));
		}        
    }
}