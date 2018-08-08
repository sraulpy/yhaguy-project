package com.yhaguy.gestion.contabilidad.subdiario;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Register;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.SubDiario;

public class SubDiarioDetalleDTO extends DTO {

	private int tipo = 0;
	private String descripcion = "";
	private double importe = 0;
	private boolean editable = false;

	private MyArray cuenta = new MyArray();

	private boolean checked = false;

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.trim().toUpperCase();
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public MyArray getCuenta() {
		return cuenta;
	}

	public void setCuenta(MyArray cuenta) {
		this.cuenta = cuenta;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getTipoCuenta() {
		String out = "";
		if (this.tipo == Configuracion.CUENTA_DEBE_KEY) {
			out = Configuracion.CUENTA_DEBE;
		}
		if (this.tipo == Configuracion.CUENTA_HABER_KEY) {
			out = Configuracion.CUENTA_HABER;
		}
		return out;
	}

	public String getCuentaDetalle() {
		return this.cuenta.getPos1() + " - " + this.cuenta.getPos2();

	}

	public String getPlanCuenta() {
		return this.cuenta.getPos4() + " - " + this.cuenta.getPos5();
	}

	public boolean isEsHaber() {
		boolean out = false;
		if (this.tipo == Configuracion.CUENTA_HABER_KEY) {
			out = true;
		}
		return out;
	}

	public String getHaber() {
		double importe;
		if (tipo == Configuracion.CUENTA_HABER_KEY) {
			System.out.println("importe : " + this.importe + "\n tipo : "
					+ this.tipo);
			importe = this.importe * this.tipo;
			String monto = this.getMisc().formatoNumeroBig(importe, false);
			//System.out.println("monto" + monto);
			return monto + "";
		}
		return "";
	}

	public String getDebe() {
		if (tipo == Configuracion.CUENTA_DEBE_KEY) {
			String monto = this.getMisc().formatoNumeroBig(this.importe, false);
			//System.out.println("monto" + monto);
			return monto + "";

		}
		return "";
	}

	// Metodo para cambiar el color si es una cuenta del tipo Haber..
	@DependsOn("checked")
	public String getColorCuentaHaber() {
		if (this.tipo == Configuracion.CUENTA_DEBE_KEY) {
			if (this.isChecked() == true) {
				return Configuracion.COLOR_HABER;
			}
		}
		return this.getMisc().rowColorCuentaHaber(this.tipo);
	}

	@Override
	public int compare(Object ob1, Object ob2) {
		SubDiarioDetalleDTO o1 = (SubDiarioDetalleDTO) ob1;
		SubDiarioDetalleDTO o2 = (SubDiarioDetalleDTO) ob2;

		int out = 0;
		long idnuevo = -1;
		long idO1 = o1.getId().longValue();
		long idO2 = o2.getId().longValue();

		// se invierte el orden,para que el negativo quede despues
		out = (Integer.compare(o1.getTipo(), o2.getTipo()) * -1);

		return out;
	}

	public static void main(String[] args) throws Exception {
		Register rr = Register.getInstance();
		SubDiario sd = new SubDiario();
		SubDiarioDTO sddto = new SubDiarioDTO();
		sd = (SubDiario) rr.getObject(SubDiario.class.getName(), 1);
		AssemblerSubDiario ass = new AssemblerSubDiario();
		sddto = (SubDiarioDTO) ass.domainToDto(sd);
		for (SubDiarioDetalleDTO d : sddto.getDetalles()) {
			System.out.println("importe : " + d.getImporte() + "\n tipo : "
					+ d.getTipo() + "\n haber : " + d.getHaber());

		}
	}
}
