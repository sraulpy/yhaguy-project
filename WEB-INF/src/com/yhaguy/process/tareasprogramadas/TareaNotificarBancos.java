package com.yhaguy.process.tareasprogramadas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class TareaNotificarBancos {
	
	static final String TAREA_NOTIFICAR_VTO_PRESTAMO_BANCARIO = "NOTIFICACION VTO. PRESTAMO BANCARIO";
	static final String USER_NOTIF = "sys";
	static final String DIRECTORIO_BASE_MRA = "/home/mraserver/project/yhaguy/";
	static final String DIRECTORIO_BASE_BAT = "/home/server/project/yhaguy-project/";
	static final String DIRECTORIO_REPORTES_MRA = "/home/mraserver/project/yhaguy/reportes/";
	static final String DIRECTORIO_REPORTES_BAT = "/home/server/project/yhaguy-project/reportes/";
	static final String EMPRESA_MRA = Configuracion.EMPRESA_MRA;
	static final String EMPRESA_BAT = Configuracion.EMPRESA_BATERIAS;
	
	static final String[] DESTINATARIOS_MRA = new String[] {
			"davida@yhaguyrepuestos.com.py", "soniat@yhaguyrepuestos.com.py" };
	
	static final String[] DESTINATARIOS_BAT = new String[] {
			"nataliac@yhaguyrepuestos.com.py", "dianaa@yhaguyrepuestos.com.py" };
	
	static final String[] COPIA_OCULTA = new String[] { "sergioa@yhaguyrepuestos.com.py" };
	static final String ASUNTO_MRA = "Vencimiento prestamos bancarios - Yhaguy M.R.A.";
	static final String ASUNTO_BAT = "Vencimiento prestamos bancarios - Yhaguy Baterias";
	
	/**
	 * notificacion por email de vencimiento de prestamos..
	 */
	private static void enviarCorreoVtoPrestamos(String empresa, String directorioReportes, 
			String directorioBase, String[] destinatarios, String asunto) {
		Configuracion.empresa = empresa;
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_VTO_PRESTAMO_BANCARIO, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_VTO_PRESTAMO_BANCARIO);
				return;
			}
			
			System.out.println("Generando el Reporte...");
			Date desde = Utiles.getFechaInicioMes();
			Date hasta = Utiles.getFechaFinMes();
			
			List<Object[]> data = new ArrayList<Object[]>();
			List<CtaCteEmpresaMovimiento> prestamos = rr.getPrestamosBancariosConSaldo(desde, hasta);
			
			if (prestamos.size() == 0) {
				return;
			}
			
			for (CtaCteEmpresaMovimiento prestamo : prestamos) {
				long dias = Utiles.diasEntreFechas(new Date(), prestamo.getFechaVencimiento());
				if (dias <= 5) {
					System.out.println("-- DIAS: " + dias);
					data.add(new Object[] {
							Utiles.getDateToString(prestamo.getFechaVencimiento(), Utiles.DD_MM_YYYY),
							prestamo.get_NroComprobante(),						
							prestamo.getTipoMovimiento().getDescripcion(), 
							prestamo.getEmpresa().getRazonSocial(),
							prestamo.getSaldo()});
				}
			}
			
			if (data.size() == 0) {
				return;
			}

			Config.DIRECTORIO_REAL_REPORTES = directorioReportes;
			Config.DIRECTORIO_BASE_REAL = directorioBase;
			
			ReporteVencimientoPrestamos rep = new ReporteVencimientoPrestamos(desde, hasta);
			rep.setDatosReporte(data);
			rep.setApaisada();
			rep.ejecutar(false);
			
			System.out.println("Reporte: " + directorioReportes + rep.getArchivoSalida());			
			
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_VTO_PRESTAMO_BANCARIO, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_VTO_PRESTAMO_BANCARIO);
				return;
			}
			
			System.out.println("enviando email...");
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion(TAREA_NOTIFICAR_VTO_PRESTAMO_BANCARIO);
			
			EnviarCorreo correo = new EnviarCorreo(tarea);
			correo.sendMessage(destinatarios, COPIA_OCULTA, asunto,
					"Notificacion de vencimiento de prestamos bancarios..", "PrestamosBancarios.pdf", directorioReportes + rep.getArchivoSalida());

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	public static void main(String[] args) {
		//TareaNotificarBancos.enviarCorreoVtoPrestamos(EMPRESA_MRA, DIRECTORIO_REPORTES_MRA, DIRECTORIO_BASE_MRA, DESTINATARIOS_MRA, ASUNTO_MRA);
		TareaNotificarBancos.enviarCorreoVtoPrestamos(EMPRESA_BAT, DIRECTORIO_REPORTES_BAT, DIRECTORIO_BASE_BAT, DESTINATARIOS_BAT, ASUNTO_BAT);
	}
}

/**
 * Reporte de Historial de Bloqueos de Clientes..
 */
class ReporteVencimientoPrestamos extends ReporteYhaguy {

	static final NumberFormat FORMATTER = new DecimalFormat("###,###,##0");
	private Date desde;
	private Date hasta;

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Vencimiento", TIPO_STRING, 40);
	static DatosColumnas col2 = new DatosColumnas("Número", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Concepto", TIPO_STRING, 40);
	static DatosColumnas col4 = new DatosColumnas("Banco", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Importe Gs.", TIPO_DOUBLE_GS, 30);

	public ReporteVencimientoPrestamos(Date desde, Date hasta) {
		this.desde = desde;
		this.hasta = hasta;
	}

	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Prestamos Bancarios");
		this.setDirectorio("banco");
		this.setNombreArchivo("Prestamo-");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}

	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {

		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(this.texto("")));
		out.add(cmp.horizontalFlowList().add(this.textoParValor("Desde", m.dateToString(this.desde, Misc.DD_MM_YYYY)))
				.add(this.textoParValor("Hasta", m.dateToString(this.hasta, Misc.DD_MM_YYYY))));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
