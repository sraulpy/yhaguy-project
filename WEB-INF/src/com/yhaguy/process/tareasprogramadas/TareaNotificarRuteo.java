package com.yhaguy.process.tareasprogramadas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.Config;
import com.coreweb.extras.reporte.DatosColumnas;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Tarea_Programada;
import com.yhaguy.util.EnviarCorreo;
import com.yhaguy.util.reporte.ReporteYhaguy;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

public class TareaNotificarRuteo {
	
	static final String TAREA_NOTIFICAR_RUTEO = "CORREO RUTEO ENVIADO";
	static final String USER_NOTIF = "sys";
	static final String DIRECTORIO_BASE_MRA = "/home/mraserver/project/yhaguy-project/";
	static final String DIRECTORIO_BASE_BAT = "/home/server/project/yhaguy-baterias/";
	static final String DIRECTORIO_BASE_CEN = "/home/server/project/yhaguy-central/";
	static final String DIRECTORIO_BASE_RPS = "/home/server/project/yhaguy-baterias/";
	static final String DIRECTORIO_REPORTES_MRA = "/home/mraserver/project/yhaguy-project/reportes/";
	static final String DIRECTORIO_REPORTES_BAT = "/home/server/project/yhaguy-baterias/reportes/";
	static final String DIRECTORIO_REPORTES_CEN = "/home/server/project/yhaguy-central/reportes/";
	static final String DIRECTORIO_REPORTES_RPS = "/home/server/project/yhaguy-baterias/reportes/";
	static final String EMPRESA = Configuracion.EMPRESA_YRSA;
	static final String EMPRESA_BAT = Configuracion.EMPRESA_GTSA;
	static final String EMPRESA_RPS = Configuracion.EMPRESA_YRPS;
	
	static final String[] DESTINATARIOS_MRA = new String[] {
			"davida@yhaguyrepuestos.com.py", "soniat@yhaguyrepuestos.com.py" };
	
	static final String[] DESTINATARIOS_BAT = new String[] {
			"nataliac@gtsa.com.py" };
	
	static final String[] DESTINATARIOS_CEN = new String[] { "soniat@yhaguyrepuestos.com.py" };
	
	static final String[] DESTINATARIOS_RPS = new String[] { "milvam@yhaguyrepresentaciones.com.py",
			"dlopez@yhaguyrepresentaciones.com.py" };
	
	static final String[] COPIA_OCULTA = new String[] { "sergioa@yhaguyrepuestos.com.py" };
	static final String ASUNTO = "Ruteo Vendedores - Yhaguy Repuestos S.A.";
	static final String ASUNTO_BAT = "Ruteo Vendedores - Grupo Toyo S.A.";
	static final String ASUNTO_RPS = "Ruteo Vendedores - Yhaguy Representaciones S.A.";
	
	/**
	 * notificacion por email de auditoria de stock..
	 */
	public static void enviarCorreoRuteo(String empresa, String directorioReportes, 
			String directorioBase, String[] destinatarios, String asunto) {
		Configuracion.empresa = empresa;
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			if (rr.getTarea_Programada(TAREA_NOTIFICAR_RUTEO, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_RUTEO);
				return;
			}

			System.out.println("Generando el Reporte..." + new Date());

			Date hoy = new Date();
			List<Object[]> list = rr.getRuteoVendedores(hoy, hoy);
			List<Object[]> data = new ArrayList<Object[]>();

			for (Object[] item : list) {
				String direccion = "";
				String ciudad = "";
				Empresa emp = rr.getEmpresa((String) item[1]);
				if (emp != null) {
					direccion = emp.getDireccion_();
					ciudad = emp.getCiudad().getDescripcion();
				}
				String vend = (String) item[0];
				String[] vendedor = vend.split("-");
				String fecha = vend.replace(vendedor[0] + "-", "");
				String[] fechaHora = fecha.split(" ");
				data.add(new Object[] { fechaHora[0], fechaHora[1], vendedor[0].toUpperCase(), item[1], direccion,
						ciudad, item[2], item[3] });
			}

			Config.DIRECTORIO_REAL_REPORTES = directorioReportes;
			Config.DIRECTORIO_BASE_REAL = directorioBase;

			ReporteRuteo rep = new ReporteRuteo();
			rep.setDatosReporte(data);
			rep.setApaisada();
			rep.ejecutar(false);

			System.out.println("Reporte: " + directorioReportes + rep.getArchivoSalida());

			if (rr.getTarea_Programada(TAREA_NOTIFICAR_RUTEO, new Date()) != null) {
				System.out.println("TAREA YA REALIZADA: " + TAREA_NOTIFICAR_RUTEO);
				return;
			}

			System.out.println("enviando email..." + new Date());
			Tarea_Programada tarea = new Tarea_Programada();
			tarea.setFecha(new Date());
			tarea.setDescripcion(TAREA_NOTIFICAR_RUTEO);

			EnviarCorreo correo = new EnviarCorreo(tarea);
			correo.sendMessage(destinatarios, COPIA_OCULTA, asunto, "Ruteo Vendedores", "RuteoVendedores.pdf",
					directorioReportes + rep.getArchivoSalida());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		//TareaNotificarStock.enviarCorreoAuditoriaStock(EMPRESA, DIRECTORIO_REPORTES_CEN, DIRECTORIO_BASE_CEN, DESTINATARIOS_CEN, ASUNTO);
		TareaNotificarRuteo.enviarCorreoRuteo(EMPRESA_RPS, DIRECTORIO_REPORTES_RPS, DIRECTORIO_BASE_RPS, DESTINATARIOS_RPS, ASUNTO_RPS);
		//TareaNotificarStock.enviarCorreoAuditoriaStock(EMPRESA_BAT, DIRECTORIO_REPORTES_BAT, DIRECTORIO_BASE_BAT, DESTINATARIOS_BAT, ASUNTO_BAT);
	}
}

/**
 * Reporte de ruteo..
 */
class ReporteRuteo extends ReporteYhaguy {
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col0 = new DatosColumnas("Fecha", TIPO_STRING, 35);
	static DatosColumnas col1 = new DatosColumnas("Hora", TIPO_STRING, 25);
	static DatosColumnas col2 = new DatosColumnas("Vendedor", TIPO_STRING, 40);
	static DatosColumnas col3 = new DatosColumnas("Cliente", TIPO_STRING);
	static DatosColumnas col4 = new DatosColumnas("Direccion", TIPO_STRING);
	static DatosColumnas col5 = new DatosColumnas("Ciudad", TIPO_STRING, 40);
	static DatosColumnas col6 = new DatosColumnas("Latitud", TIPO_STRING, 35);
	static DatosColumnas col7 = new DatosColumnas("Longitud", TIPO_STRING, 35);
	
	public ReporteRuteo() {
	}
	
	static {
		cols.add(col0);
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
		cols.add(col5);
		cols.add(col6);
		cols.add(col7);
	}

	@Override
	public void informacionReporte() {
		this.setDirectorio("ventas");
		this.setNombreArchivo("Ruteo-");
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
