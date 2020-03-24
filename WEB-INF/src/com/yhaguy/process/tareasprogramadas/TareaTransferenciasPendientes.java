package com.yhaguy.process.tareasprogramadas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class TareaTransferenciasPendientes {

	static final String TAREA_NOTIFICAR_TRANSFERENCIA = "CORREO TRANSFERENCIAS PENDIENTES ENVIADO";
	static final String USER_NOTIF = "sys";
	static final String DIRECTORIO_BASE_CEN = "/home/server/project/yhaguy-central/";
	static final String DIRECTORIO_REPORTES_CEN = "/home/server/project/yhaguy-central/reportes/";
	static final String EMPRESA = Configuracion.EMPRESA_YRSA;

	static final String[] DESTINATARIOS_CEN = new String[] { "soniat@yhaguyrepuestos.com.py",
			"oscarv@yhaguyrepuestos.com.py", "jorgeo@yhaguyrepuestos.com.py", "marcelov@yhaguyrepuestos.com.py" };

	static final String[] COPIA_OCULTA = new String[] { "sergioa@yhaguyrepuestos.com.py" };
	static final String ASUNTO = "Transferencias Pendientes - Yhaguy Repuestos S.A.";

	/**
	 * notificacion por email de auditoria de transferencias..
	 */
	public static void enviarCorreoTransferenciasPendientes(String empresa, String directorioReportes,
			String directorioBase, String[] destinatarios, String asunto) {
		Configuracion.empresa = empresa;
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_TRANSFERENCIA, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_TRANSFERENCIA);
				return;
			}

			System.out.println("Generando el Reporte..." + new Date());
			
			List<Object[]> data = new ArrayList<Object[]>();
			List<Transferencia> transfs = rr.getTransferenciasPendientes();
			for (Transferencia transf : transfs) {
				Object[] obj = new Object[] { Utiles.getDateToString(transf.getFechaCreacion(), Utiles.DD_MM_YYYY),
						transf.getNumero(), transf.getTransferenciaTipo().getDescripcion(),
						transf.getDepositoSalida().getDescripcion() };
				data.add(obj);
			}		

			Config.DIRECTORIO_REAL_REPORTES = directorioReportes;
			Config.DIRECTORIO_BASE_REAL = directorioBase;

			ReporteTransferenciaPendientes rep = new ReporteTransferenciaPendientes();
			rep.setDatosReporte(data);
			rep.ejecutar(false);

			System.out.println("Reporte: " + directorioReportes + rep.getArchivoSalida());

			if (rr.getTarea_Programada(TAREA_NOTIFICAR_TRANSFERENCIA, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_TRANSFERENCIA);
				return;
			}

			System.out.println("enviando email..." + new Date());
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion(TAREA_NOTIFICAR_TRANSFERENCIA);

			EnviarCorreo correo = new EnviarCorreo(tarea);
			correo.sendMessage(destinatarios, COPIA_OCULTA, asunto, "Auditoria de Stock", "AuditoriaStock.pdf",
					directorioReportes + rep.getArchivoSalida());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TareaTransferenciasPendientes.enviarCorreoTransferenciasPendientes(EMPRESA, DIRECTORIO_REPORTES_CEN,
				DIRECTORIO_BASE_CEN, DESTINATARIOS_CEN, ASUNTO);
	}
}

/**
 * Reporte de transferencias pendientes..
 */
class ReporteTransferenciaPendientes extends ReporteYhaguy {

	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING);
	static DatosColumnas col1 = new DatosColumnas("Número", TIPO_STRING);
	static DatosColumnas col2 = new DatosColumnas("Tipo", TIPO_STRING);
	static DatosColumnas col3 = new DatosColumnas("Depósito", TIPO_STRING);

	public ReporteTransferenciaPendientes() {
	}

	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Transferencias Pendientes");
		this.setDirectorio("ventas");
		this.setNombreArchivo("Transf_pendientes-");
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

		return out;
	}
}
