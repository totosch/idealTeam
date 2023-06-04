package presenter;

import model.Model;
import model.Auxiliares;
import model.Integrante;
import view.AccionSimultanea;
import view.IntegranteView;
import view.Simulacion;
import view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Presenter {
    private Model model;
    private View view;

    public Presenter(View view, Model model) {
        this.view = view;
        this.model = model;
        
        HashMap<String, Integer> cantidadPorPuesto = new HashMap<String, Integer>();
        
        for (String rol: Integrante.roles) {
        	cantidadPorPuesto.put(rol, 1);
        }
        
        model.registrarCantidadPorPuesto(cantidadPorPuesto);
        
        view.agregarActionListenerBoton(new SolverListener(),view.getBotonCorrerSolver());
        view.agregarActionListenerBoton(new FetchListener(),view.getBotonBuscarEmpleados());
        view.agregarActionListenerBoton(new RelacionesListener(),view.getBotonIncompatibilidades());
    }

    public void startGame() {
        view.inicializarView();
    }
    
    public class FetchListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Simulacion<List<IntegranteView>> simulacion = new Simulacion<List<IntegranteView>>(
					view.getBarraProgresoEmpleados(), 
					new AccionSimultanea<List<IntegranteView>>() {

						@Override
						public List<IntegranteView> accion() {
							// TODO Auto-generated method stub
							return enviarEmpleados();
						}

						@Override
						public void obtenerEnView(List<IntegranteView> arg) {
							view.popularEmpleadosTotales(arg);
						}
			});
			
			simulacion.execute();
		}
		
		private List<IntegranteView> enviarEmpleados(){
			try {
				List<Integrante> integrantes = model.crearIntegrantes(10);
				List<IntegranteView> empleados = new ArrayList<IntegranteView>();
				integrantes.forEach(integrante -> empleados.add(new IntegranteView(
						integrante.getValor(), 
						integrante.getNombre(), 
						integrante.getRol())));
				
				view.popularEmpleadosTotales(empleados);
				
				return empleados;
			} catch (Exception ex) {
				view.mostrarMensajeEmergente(ex.getMessage());
			}
			return null;
		}
    	
    }
    
    public class RelacionesListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Simulacion<HashSet<HashSet<IntegranteView>>> simulacion = new Simulacion<HashSet<HashSet<IntegranteView>>>(
					view.getBarraProgresoIncompatiblidades(), 
					new AccionSimultanea<HashSet<HashSet<IntegranteView>>>() {

						@Override
						public HashSet<HashSet<IntegranteView>> accion() {
							// TODO Auto-generated method stub
							return enviarRelaciones();
						}

						@Override
						public void obtenerEnView(HashSet<HashSet<IntegranteView>> arg) {
							view.popularIncompatibilidades(arg);
						}
			});
			
			simulacion.execute();
		}
		
		private HashSet<HashSet<IntegranteView>> enviarRelaciones() {
			try {
				model.establecerRelaciones();
				List<Integrante> integrantes = model.getIntegrantes();
				Set<Set<Integrante>> relacionesMalasSet = Auxiliares.parseRelaciones(integrantes);
				
				HashSet<HashSet<IntegranteView>> relacionesMalasView = new HashSet<HashSet<IntegranteView>>();
				relacionesMalasSet.forEach(tuplaIntegrante -> {
					HashSet<Integrante> tuplaIntegranteParseada = new HashSet<Integrante>(tuplaIntegrante);
					HashSet<IntegranteView> tuplaIntegranteView = new HashSet<IntegranteView>();
										
					tuplaIntegranteParseada.forEach(integrante -> tuplaIntegranteView.add(new IntegranteView(
							integrante.getValor(), 
							integrante.getNombre(), 
							integrante.getRol())));
					
					relacionesMalasView.add(tuplaIntegranteView);
				});
				
				view.popularIncompatibilidades(relacionesMalasView);
				
				return relacionesMalasView;
			} catch (Exception ex) {
				view.mostrarMensajeEmergente(ex.getMessage());
			}
			return null;
		}
    	
    }
    
    public class SolverListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				
				model.resolverProblema();
			} catch (Exception ex) {
				view.mostrarMensajeEmergente(ex.getMessage());
			}
		}
    	
    }
}
