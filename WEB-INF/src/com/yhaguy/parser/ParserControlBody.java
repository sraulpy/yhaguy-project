package com.yhaguy.parser;

import org.zkoss.bind.annotation.Command;

import com.yhaguy.util.population.UsuarioPerfilParser;

public class ParserControlBody {
	
	@Command
	public void parser() throws Exception {
		UsuarioPerfilParser.loadMenuConfigBoton();
	}

}
