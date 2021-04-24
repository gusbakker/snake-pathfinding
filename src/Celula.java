import java.awt.Color;
import java.awt.Point;



public class Celula {
		
		private Color color;
		private Point position;
		private boolean flag;
		private boolean bloqueado;
		private boolean ocupado;
		private boolean selected;
		
		public Celula(int x, int y) {
			position = new Point(x,y);
			setOcupado(false);
		}
		
		
		public void setPosition(Point point){
			position.setLocation(point);
		}
		public Point getPosition(){
			return position;
		}


		public boolean isOcupado() {
			return ocupado;
		}


		public void setOcupado(boolean ocupado) {
			this.ocupado = ocupado;
		}


		public boolean isSelected() {
			return selected;
		}


		public void setSelected(boolean selected) {
			this.selected = selected;
		}


		public boolean isBloqueado() {
			return bloqueado;
		}


		public void setBloqueado(boolean bloqueado) {
			this.bloqueado = bloqueado;
		}


		public boolean isFlag() {
			return flag;
		}


		public void setFlag(boolean flag) {
			this.flag = flag;
		}


		public Color getColor() {
			return color;
		}


		public void setColor(Color color) {
			this.color = color;
		}		

}
