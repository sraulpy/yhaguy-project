package com.yhaguy.gestion.venta.metas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.VentaMeta;
import com.yhaguy.util.Utiles;

public class VentaMetaViewModel extends SimpleViewModel {
	
	static final String ZUL_EDIT_META = "/yhaguy/gestion/venta/editVentaMeta.zul";
	
	private int size = 0;
	
	private MyArray selectedItem;
	
	private Window win;

	@Init(superclass = true)
	public void init() {	
	}	
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void addVentaMeta() throws Exception {
		this.showVentaMeta(true);
	}
	
	@Command
	@NotifyChange("*")
	public void editVentaMeta() throws Exception {
		this.showVentaMeta(false);
	}
	
	@Command
	public void print() throws Exception {
		this.imprimir();
	}
	
	/**
	 * despliega la ventana de datos de la meta de venta..
	 */
	private void showVentaMeta(boolean add) throws Exception {
		if (add) this.inicializarSelectedItem();
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setCheckAC(new ValidadorVentaMeta(add));
		wp.setHigth("350px");
		wp.setWidth("700px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " Meta de Venta..");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_EDIT_META);

		if (wp.isClickAceptar()) {
			this.saveVentaMeta(add);
			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
		this.selectedItem = null;
	}
	
	/**
	 * graba en la bd una nueva meta..
	 */
	private void saveVentaMeta(boolean add) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		MyPair func_ = (MyPair) this.selectedItem.getPos15();
		Funcionario func = rr.getFuncionarioById(func_.getId());
		VentaMeta vm = new VentaMeta();
		if (!add) {
			vm = (VentaMeta) rr.getObject(VentaMeta.class.getName(), this.selectedItem.getId());
		}
		vm.setEnero((double) this.selectedItem.getPos3());		
		vm.setFebrero((double) this.selectedItem.getPos4());
		vm.setMarzo((double) this.selectedItem.getPos5());
		vm.setAbril((double) this.selectedItem.getPos6());
		vm.setMayo((double) this.selectedItem.getPos7());
		vm.setJunio((double) this.selectedItem.getPos8());	
		vm.setJulio((double) this.selectedItem.getPos9());
		vm.setAgosto((double) this.selectedItem.getPos10());
		vm.setSetiembre((double) this.selectedItem.getPos11());
		vm.setOctubre((double) this.selectedItem.getPos12());
		vm.setNoviembre((double) this.selectedItem.getPos13());
		vm.setDiciembre((double) this.selectedItem.getPos14());					
		vm.setPeriodo((String) this.selectedItem.getPos2());
		func.getMetas().add(vm);
		rr.saveObject(func, this.getLoginNombre());		
	}
	
	/**
	 * inicializa el myarray selectedItem..
	 */
	private void inicializarSelectedItem() {
		this.selectedItem = new MyArray();
		this.selectedItem.setPos1("");
		this.selectedItem.setPos2(Utiles.getDateToString(new Date(), "yyyy"));
		this.selectedItem.setPos3(0.0);
		this.selectedItem.setPos4(0.0);
		this.selectedItem.setPos5(0.0);
		this.selectedItem.setPos6(0.0);
		this.selectedItem.setPos7(0.0);
		this.selectedItem.setPos8(0.0);
		this.selectedItem.setPos9(0.0);
		this.selectedItem.setPos10(0.0);
		this.selectedItem.setPos11(0.0);
		this.selectedItem.setPos12(0.0);
		this.selectedItem.setPos13(0.0);
		this.selectedItem.setPos14(0.0);
		this.selectedItem.setPos15(new MyPair());
	}
	
	/**
	 * reporte de metas de ventas..
	 */
	private void imprimir() throws Exception {
		List<Object[]> data = new ArrayList<Object[]>();
		
		for (MyArray meta : this.getMetas()) {
			data.add(new Object[] { meta.getPos1(), meta.getPos3(),
					meta.getPos4(), meta.getPos5(), meta.getPos6(),
					meta.getPos7(), meta.getPos8(), meta.getPos9(),
					meta.getPos10(), meta.getPos11(), meta.getPos12(),
					meta.getPos13(), meta.getPos14() });
		}
		
		String source = com.yhaguy.gestion.reportes.formularios.ReportesViewModel.SOURCE_METAS_VENDEDOR;
		Map<String, Object> params = new HashMap<String, Object>();
		JRDataSource dataSource = new MetasPorVendedorDataSource(data);
		params.put("Titulo", "Metas por Vendedor");
		params.put("Usuario", getUs().getNombre());
		imprimirJasper(source, params, dataSource, null);
	}
	
	/**
	 * Despliega el reporte en un pdf para su impresion..
	 */
	public void imprimirJasper(String source, Map<String, Object> parametros,
			JRDataSource dataSource, Object[] format) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("source", source);
		params.put("parametros", parametros);
		params.put("dataSource", dataSource);
		params.put("format", format);

		this.win = (Window) Executions
				.createComponents(
						com.yhaguy.gestion.reportes.formularios.ReportesViewModel.ZUL_REPORTES,
						this.mainComponent, params);
		this.win.doModal();
	}
	
	/**
	 * Validador edicion de venta meta..
	 */
	class ValidadorVentaMeta implements VerificaAceptarCancelar {

		String message;
		boolean add;
		
		public ValidadorVentaMeta(boolean add) {
			this.add = add;
		}
		
		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.message = "No se puede completar la operación debido a:";			
			if (add) {
				try {
					RegisterDomain rr = RegisterDomain.getInstance();
					Funcionario func = rr.getFuncionarioById(((MyPair)selectedItem.getPos15()).getId());
					Set<VentaMeta> metas = func.getMetas();
					for (VentaMeta meta : metas) {
						if (meta.getPeriodo().equals(selectedItem.getPos2())) {
							out = false;
							this.message += "\n - No se permiten ítems duplicados..";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			return out;
		}

		@Override
		public String textoVerificarAceptar() {
			return this.message;
		}

		@Override
		public boolean verificarCancelar() {
			return true;
		}

		@Override
		public String textoVerificarCancelar() {
			return "Error al cancelar..";
		}
	}
	
	
	/**
	 * GETS / SETS
	 */
	
	/**
	 * @return los vendedores..
	 */
	public List<MyArray> getMetas() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Funcionario> vends = rr.getVendedores();
		for (Funcionario func : vends) {
			for (VentaMeta meta : func.getMetas()) {
				MyArray my = new MyArray();
				my.setId(meta.getId());
				my.setPos1(func.getRazonSocial().toUpperCase());
				my.setPos2(meta.getPeriodo());
				my.setPos3(meta.getEnero());
				my.setPos4(meta.getFebrero());
				my.setPos5(meta.getMarzo());
				my.setPos6(meta.getAbril());
				my.setPos7(meta.getMayo());
				my.setPos8(meta.getJunio());
				my.setPos9(meta.getJulio());
				my.setPos10(meta.getAgosto());
				my.setPos11(meta.getSetiembre());
				my.setPos12(meta.getOctubre());
				my.setPos13(meta.getNoviembre());
				my.setPos14(meta.getDiciembre());
				my.setPos15(new MyPair(func.getId(), func.getRazonSocial()));
				out.add(my);
			}
		}
		this.size = out.size();
		BindUtils.postNotifyChange(null, null, this, "size");
		return out;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public MyArray getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyArray selectedItem) {
		this.selectedItem = selectedItem;
	}	
}	

/**
 * DataSource de Metas por Vendedor..
 */
class MetasPorVendedorDataSource implements JRDataSource {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");

	List<Object[]> values = new ArrayList<Object[]>();
	Map<String, Double> totales = new HashMap<String, Double>();
	
	public MetasPorVendedorDataSource(List<Object[]> values) {
		this.values = values;
		Collections.sort(this.values, new Comparator<Object[]>() {
			@Override
			public int compare(Object[] o1, Object[] o2) {
				String val1 = (String) o1[0];
				String val2 = (String) o2[0];
				int compare = val1.compareTo(val2);				
				return compare;
			}
		});
		totales.put("Ene", 0.0);
		totales.put("Feb", 0.0);
		totales.put("Mar", 0.0);
		totales.put("Abr", 0.0);
		totales.put("May", 0.0);
		totales.put("Jun", 0.0);
		totales.put("Jul", 0.0);
		totales.put("Ago", 0.0);
		totales.put("Set", 0.0);
		totales.put("Oct", 0.0);
		totales.put("Nov", 0.0);
		totales.put("Dic", 0.0);
		for (Object[] value : values) {
			Double ene = totales.get("Ene");
			Double feb = totales.get("Feb");
			Double mar = totales.get("Mar");
			Double abr = totales.get("Abr");
			Double may = totales.get("May");
			Double jun = totales.get("Jun");
			Double jul = totales.get("Jul");
			Double ago = totales.get("Ago");
			Double set = totales.get("Set");
			Double oct = totales.get("Oct");
			Double nov = totales.get("Nov");
			Double dic = totales.get("Dic");
			ene += (double) value[1];
			feb += (double) value[2];
			mar += (double) value[3];
			abr += (double) value[4];
			may += (double) value[5];
			jun += (double) value[6];
			jul += (double) value[7];
			ago += (double) value[8];
			set += (double) value[9];
			oct += (double) value[10];
			nov += (double) value[11];
			dic += (double) value[12];
			totales.put("Ene", ene); totales.put("Feb", feb);
			totales.put("Mar", mar); totales.put("Abr", abr);
			totales.put("May", may); totales.put("Jun", jun);
			totales.put("Jul", jul); totales.put("Ago", ago);
			totales.put("Set", set); totales.put("Oct", oct);
			totales.put("Nov", nov); totales.put("Dic", dic);
		}
	}

	private int index = -1;

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		Object[] det = this.values.get(index);

		if ("Vendedor".equals(fieldName)) {
			value = det[0];
		} else if ("Ene".equals(fieldName)) {
			value = FORMATTER.format(det[1]);
		} else if ("Feb".equals(fieldName)) {
			value = FORMATTER.format(det[2]);
		} else if ("Mar".equals(fieldName)) {
			value = FORMATTER.format(det[3]);
		} else if ("Abr".equals(fieldName)) {
			value = FORMATTER.format(det[4]);			
		} else if ("May".equals(fieldName)) {
			value = FORMATTER.format(det[5]);
		} else if ("Jun".equals(fieldName)) {
			value = FORMATTER.format(det[6]);
		} else if ("Jul".equals(fieldName)) {
			value = FORMATTER.format(det[7]);
		} else if ("Ago".equals(fieldName)) {
			value = FORMATTER.format(det[8]);
		} else if ("Set".equals(fieldName)) {
			value = FORMATTER.format(det[9]);
		} else if ("Oct".equals(fieldName)) {
			value = FORMATTER.format(det[10]);
		} else if ("Nov".equals(fieldName)) {
			value = FORMATTER.format(det[11]);
		} else if ("Dic".equals(fieldName)) {
			value = FORMATTER.format(det[12]);
		} else if ("Tot_1".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Ene"));
		} else if ("Tot_2".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Feb"));
		} else if ("Tot_3".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Mar"));
		} else if ("Tot_4".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Abr"));
		} else if ("Tot_5".equals(fieldName)) {
			value = FORMATTER.format(totales.get("May"));
		} else if ("Tot_6".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Jun"));
		} else if ("Tot_7".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Jul"));
		} else if ("Tot_8".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Ago"));
		} else if ("Tot_9".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Set"));
		} else if ("Tot_10".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Oct"));
		} else if ("Tot_11".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Nov"));
		} else if ("Tot_12".equals(fieldName)) {
			value = FORMATTER.format(totales.get("Dic"));
		} 
		return value;
	}

	@Override
	public boolean next() throws JRException {
		if (index < this.values.size() - 1) {
			index++;
			return true;
		}
		return false;
	}
}
