package com.yhaguy.charts.ventas;

import java.util.Date;

import org.zkoss.chart.Charts;
import org.zkoss.chart.Color;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.yhaguy.domain.HistoricoCobranzaDiaria;
import com.yhaguy.domain.HistoricoCobranzaVendedor;
import com.yhaguy.domain.HistoricoVentaDiaria;
import com.yhaguy.domain.HistoricoVentaVendedor;
import com.yhaguy.domain.RegisterDomain;
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
        
        Double ventas = new Double(0);
        Double cobranzas = new Double(0);
        Double meta = new Double(0);
        Double dia = new Double(0);
        Double c_dia = new Double(0);
        
        RegisterDomain rr = RegisterDomain.getInstance();
        int anho = Integer.parseInt(Utiles.getAnhoActual());
		int mes = Integer.parseInt(Utiles.getMesActual());
        HistoricoVentaVendedor hist = rr.getHistoricoVentaVendedor(anho, mes, 0);
        HistoricoVentaDiaria hist_ = rr.getHistoricoVentaDiaria(new Date(), 0);
        HistoricoCobranzaVendedor c_hist = rr.getHistoricoCobranzaVendedor(anho, mes, 0);
        HistoricoCobranzaDiaria c_hist_ = rr.getHistoricoCobranzaDiaria(new Date(), 0);
        
        if (hist_ != null) dia = (hist_.getTotal_venta() - hist_.getTotal_notacredito());
        if (c_hist_ != null) c_dia = (c_hist_.getTotal_cobranza());
        
        if (hist != null) {
			ventas = (hist.getTotal_venta() - hist.getTotal_notacredito());
			meta = hist.getMeta();
		}
        
        if (c_hist != null) cobranzas = (c_hist.getTotal_cobranza());
        
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