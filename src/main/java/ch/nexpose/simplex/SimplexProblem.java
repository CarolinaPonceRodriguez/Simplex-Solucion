package ch.nexpose.simplex;

import ch.nexpose.simplex.types.ConstraintType;
import ch.nexpose.simplex.types.OptimisationType;
import com.sun.xml.ws.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cansik on 22/11/15.
 */
public class SimplexProblem {
    private OptimisationType optimisationType;
    private List<SimplexCoefficient> listaDeCoeficientes;
    private List<SimplexConstraint> listaDeRestricciones;
	private int cantidadDeRestricciones;
	private int cantidadDeVariables;

    public SimplexProblem(){
    	this.listaDeCoeficientes = new ArrayList<>();
    	this.listaDeRestricciones = new ArrayList<>();
    }

    public void parse(String data){
        int unaPosicion = 0;
        String[] arrayDeDatos = this.recuperarDatosDe(data);
        cantidadDeVariables = Integer.parseInt(arrayDeDatos[unaPosicion++]);
        cantidadDeRestricciones = Integer.parseInt(arrayDeDatos[unaPosicion++]);
        optimisationType = this.tipoDeOptimizacionSegun(arrayDeDatos[unaPosicion++]);
        inicializarCoeficientesSegun(arrayDeDatos, unaPosicion);
        unaPosicion = unaPosicion + (this.cantidadDeVariables*2);
        inicializarRestriccionesSegun(arrayDeDatos, unaPosicion);
    }

    private String[] recuperarDatosDe(String unInput) {
    	return Arrays.stream(unInput.replace("\n", ";").split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }
    
    private OptimisationType tipoDeOptimizacionSegun(String unValor) {
    	return OptimisationType.valueOf(StringUtils.capitalize(unValor));
    }
    
    private void inicializarCoeficientesSegun(String[] datos, int unaPosicion) {
       agregarCoeficientesSegun(datos, unaPosicion);
       settearNegativdadDeCoeficientesSegun(datos, unaPosicion+this.cantidadDeVariables);
    }
    
    private void agregarCoeficientesSegun(String[] datos, int unaPosicion) {
    	 for(int i = 0; i < this.cantidadDeVariables + 1; i++){
    		 this.listaDeCoeficientes.add(new SimplexCoefficient(Double.parseDouble(datos[unaPosicion++])));
         }
    }
    
    private void settearNegativdadDeCoeficientesSegun(String[] datos, int unaPosicion) {
    	int index;
    	for(index = 0; index < this.listaDeCoeficientes.size() -1; index++) {
    		this.listaDeCoeficientes.get(index).setNotNegative(Boolean.parseBoolean(datos[unaPosicion++]));
    	}
    }
    
    private void inicializarRestriccionesSegun(String[] datos, int unaPosicion) {
    	for(int i = 0; i < this.cantidadDeRestricciones; i++){
    		unaPosicion++;
            SimplexConstraint unaRestriccion = new SimplexConstraint(cantidadDeVariables);
            switch (datos[unaPosicion])
            {
                case "<=":
                    unaRestriccion.setConstraintType(ConstraintType.LessThanEquals);
                    break;

                case "=":
                    unaRestriccion.setConstraintType(ConstraintType.Equals);
                    break;

                case ">=":
                    unaRestriccion.setConstraintType(ConstraintType.GreaterThanEquals);
                    break;
            }
            for(int k = 0; k < cantidadDeVariables + 1; k++){
            	unaPosicion++;
                unaRestriccion.getCoefficients()[k] = (Double.parseDouble(datos[unaPosicion]));
            }
            this.listaDeRestricciones.add(unaRestriccion);
        }
    }
    
    public OptimisationType getOptimisationType() {
        return optimisationType;
    }

    public void setOptimisationType(OptimisationType optimisationType) {
        this.optimisationType = optimisationType;
    }

    public SimplexCoefficient[] getCoefficients() {
    	SimplexCoefficient[] coeficientes = new SimplexCoefficient[this.listaDeCoeficientes.size()];
    	return this.listaDeCoeficientes.toArray(coeficientes);
    }

    public SimplexConstraint[] getConstraints() {
    	SimplexConstraint[] restricciones = new SimplexConstraint[this.listaDeRestricciones.size()];
    	return this.listaDeRestricciones.toArray(restricciones);
    }

    public double[] getSlackVariables(){
        double[] slackVars = new double[this.listaDeCoeficientes.size()];
        int index;
        for(index = 0; index < this.listaDeCoeficientes.size();index++) {
        	slackVars[index] = this.listaDeCoeficientes.get(index).getValue();
        }
        return slackVars;
    }

    public void convertInequation(){
    	this.listaDeCoeficientes.stream().forEach(c -> c.setValue(c.getValue()*-1) );
    }

    public void convertEqualsConstraints(){
        ArrayList<SimplexConstraint> cons = new ArrayList<>();
        int index;
        for(index = 0; index < this.listaDeRestricciones.size(); index++) {
        	SimplexConstraint unaRestriccion = this.listaDeRestricciones.get(index);
        	if(unaRestriccion.getConstraintType() != ConstraintType.Equals) {
                cons.add(unaRestriccion);
                continue;
            }
            cons.add(new SimplexConstraint(unaRestriccion, ConstraintType.LessThanEquals));
            cons.add(new SimplexConstraint(unaRestriccion, ConstraintType.GreaterThanEquals));
        }
        this.listaDeRestricciones = cons;
    }
}
