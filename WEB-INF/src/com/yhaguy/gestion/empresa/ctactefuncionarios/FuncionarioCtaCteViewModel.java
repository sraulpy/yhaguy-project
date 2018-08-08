package com.yhaguy.gestion.empresa.ctactefuncionarios;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.ViewPdf;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.FuncionarioCtaCteDetalle;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class FuncionarioCtaCteViewModel extends SimpleViewModel {
	
	final static String[] ATT_FUNCIONARIO = { "empresa.nombre" };
	final static String[] COLUMNAS = { "Apellido y Nombre" };
	
	private MyArray funcionario = new MyArray();
	private Date desde;
	private Date hasta;
	private double totalGs;

	@Init(superclass = true)
	public void init() {
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void buscarFuncionario() throws Exception {
		this.buscarFuncionario_();
	}
	
	@Command
	public void imprimir() throws Exception {
		this.imprimir_();
	}
	
	
	/***************** FUNCIONES *****************/
	
	private void buscarFuncionario_() throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Funcionario.class);
		b.setAtributos(ATT_FUNCIONARIO);
		b.setNombresColumnas(COLUMNAS);
		b.setTitulo("Buscar Funcionario");
		b.setWidth("600px");
		b.addOrden("empresa.nombre");
		b.show((String) this.funcionario.getPos1());
		if (b.isClickAceptar()) {
			this.funcionario = b.getSelectedItem();
		}
	}
	
	/**
	 * Muestra en PDF el reporte..
	 */
	private void imprimir_() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();

		for (MyArray item : this.getMovimientos()) {
			Object[] det = new Object[] { item.getPos1(), item.getPos2(),
					item.getPos3(), item.getPos4() };
			data.add(det);
		}

		Map<Integer, String> params = new HashMap<Integer, String>();
		params.put(ReporteFunCtaCte.FUNCIONARIO,
				(String) this.funcionario.getPos1());
		params.put(ReporteFunCtaCte.DESDE,
				this.m.dateToString(this.desde, Misc.YYYY_MM_DD));
		params.put(ReporteFunCtaCte.HASTA,
				this.m.dateToString(this.hasta, Misc.YYYY_MM_DD));

		ReporteYhaguy rep = new ReporteFunCtaCte(params);
		rep.setDatosReporte(data);

		ViewPdf vp = new ViewPdf();
		vp.setBotonCancelar(false);
		vp.setBotonImprimir(false);
		vp.showReporte(rep, this);
	}
	
	/*********************************************/
	
	
	/****************** GET/SET ******************/
	
	@DependsOn({ "funcionario", "desde", "hasta" })
	public List<MyArray> getMovimientos() throws Exception {
		
		this.totalGs = 0;
		
		if(this.hasta == null)
			return new ArrayList<MyArray>();
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<FuncionarioCtaCteDetalle> list = rr.getMovimientosFuncionario(
				this.funcionario.getId(), this.desde, this.hasta);
		List<MyArray> out = new ArrayList<MyArray>();
		for (FuncionarioCtaCteDetalle mov : list) {
			MyArray det = new MyArray();
			det.setId(mov.getId());
			det.setPos1(mov.getFecha());
			det.setPos2(mov.getNroComprobante());
			det.setPos3(mov.getDescripcion());
			det.setPos4(mov.getMontoGs());
			det.setPos5(mov.isAnulado());
			out.add(det);
			this.totalGs += mov.getMontoGs();
		}
		BindUtils.postNotifyChange(null, null, this, "totalGs");
		return out;
	}
	
	/*********************************************/
	
	
	/****************** GET/SET ******************/

	public MyArray getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(MyArray funcionario) {
		this.funcionario = funcionario;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public double getTotalGs() {
		return totalGs;
	}

	public void setTotalGs(double totalGs) {
		this.totalGs = totalGs;
	}	
}

/**
 * Reporte Funcionario Cta. Cte.
 */
class ReporteFunCtaCte extends ReporteYhaguy {
	
	private Map<Integer, String> params;
	
	static final int FUNCIONARIO = 1;
	static final int DESDE = 2;
	static final int HASTA = 3;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Fecha", TIPO_DATE);
	static DatosColumnas col2 = new DatosColumnas("Nro. Comprobante", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Descripción", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, true);
	
	public ReporteFunCtaCte(Map<Integer, String> params) {
		this.params = params;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {	
		this.setTitulo("Listado de Movimientos por Funcionario");
		this.setDirectorio("funcionarios");
		this.setNombreArchivo("FuncDetalle-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		
		String funcionario = this.params.get(FUNCIONARIO);
		String desde = this.params.get(DESDE);
		String hasta = this.params.get(HASTA);
		
		VerticalListBuilder out = cmp.verticalList();		
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Funcionario", funcionario)));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Fecha Desde", desde)));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Fecha Hasta", hasta)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		
		return out;
	}
}
