package fr.klemek.minimario;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MarioAI {
	
	//times
	private static final int MIN_STATE_TIME = 1000;
	private static final int MAX_STILL_TIME = 10000-MIN_STATE_TIME;
	private static final int MAX_WALKING_TIME = 5000-MIN_STATE_TIME;
	private static final int MAX_RUNNING_TIME = 3000-MIN_STATE_TIME;
	
	//speeds
	private static final float WALK_SPEED = 0.5f;
	private static final float RUN_SPEED = 1.5f;
	private static final float JUMP_SPEED_X = 0.25f;
	private static final float GRAVITY = 0.25f;
	private static final float MIN_JUMP_SPEED_Y = 1f;
	private static final float MAX_JUMP_SPEED_Y = 5f-MIN_JUMP_SPEED_Y;
	
	//tiles
	private static final int MARIO_STILL = 0;
	private static final int MARIO_WALKING_2 = 1;
	private static final int MARIO_JUMPING = 2;
	private static final int MARIO_FALLING = 3;
	private static final int MARIO_TURNING = 4;
	private static final int MARIO_BACK = 5;
	private static final int MARIO_DUCK = 6;
	private static final int MARIO_WIN = 7;
	private static final int MARIO_RUNNING_1 = 8;
	private static final int MARIO_RUNNING_2 = 9;
	private static final int MARIO_LOOK_UP = 10;
	private static final int MARIO_LOOSING = 11;
	
	private enum State{
		STILL, STILL_BACK, STILL_WIN, STILL_DUCK, STILL_LOOK_UP, WALKING, RUNNING, JUMPING, LOOSING
	}
	
	private int id;
	
	private State state;
	private int time, time2;
	private float spdy, maxspdy;
	
	private Point2D.Float pos;
	
	private boolean left, wait, turn, kicked, invicible;
	
	private final int minx,maxx;
	private int sizex, sizey, speedf;
	
	//constructor
	
	public MarioAI(int sizex, int sizey, int speedf){
		this.id = Utils.nextInt();
		this.minx = Utils.getMinX();
		this.maxx = Utils.getMaxX();
		this.sizex = sizex;
		this.sizey = sizey;
		this.speedf = speedf;
		this.state = State.STILL;
		this.time = 0;
		this.pos = new Point2D.Float();
	}
	
	//functions
	
	public void moved(Point newpos){
		this.pos = new Point2D.Float(newpos.x, newpos.y);
		this.state = State.LOOSING;
		this.time2 = time;
	}
	
	public Point refresh(int refresh){
		time += refresh;
		
		Point2D.Float speed = new Point2D.Float();
		
		if(this.state != State.JUMPING){
			this.kicked = false;
			this.invicible = false;
		}
			
		
		switch(this.state){
		case JUMPING:
			speed.x = (this.left?-1f:1f)*speedf*JUMP_SPEED_X;
			this.spdy += GRAVITY*speedf;
			speed.y = spdy;
			if(this.spdy>=this.maxspdy){
				this.spdy = 0;
				int randi = Utils.nextInt(100);
				if(randi<40){ //0-39 - 40%
					this.state = State.STILL;
				}else if(randi<70){ //40-69 - 30%
					this.state = State.WALKING;
				}else{ //70-99 - 30%
					this.state = State.RUNNING;
				}
			}
			break;
		case STILL_BACK:
		case STILL_LOOK_UP:
		case STILL_WIN:
		case STILL_DUCK:
		case STILL:
			if(this.time2<this.time){
				if(this.wait){
					this.wait = false;
					int randi = Utils.nextInt(100);
					if(randi<70){ //0-69 - 70%
						this.state = State.WALKING;
					}else if(randi<90){ //70-89 - 20%
						this.spdy = -Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y-speedf*MIN_JUMP_SPEED_Y;
						this.maxspdy = Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y+speedf*MIN_JUMP_SPEED_Y;
						this.state = State.JUMPING;
					}else{ //90-99 - 10%
						boolean nextSide = Utils.nextBoolean();
						this.turn = this.left != nextSide;
						this.left = nextSide;
						this.state = State.RUNNING;
					}
				}else{
					this.time2 = this.time+Utils.nextInt(MAX_STILL_TIME)+MIN_STATE_TIME;
					this.wait = true;
					int randi = Utils.nextInt(100);
					if(randi<75){ //0-74 - 75%
						this.state = State.STILL;
					}else if(randi<85){ //75-84 - 10 %
						this.state = State.STILL_LOOK_UP;
					}else if(randi<95){ //85-94 - 10%
						this.state = State.STILL_DUCK;
					}else if(randi<99){ //95-99 - 4%
						this.state = State.STILL_BACK;
					}else{ //99 - 1%
						this.state = State.STILL_WIN;
					}
				}
			}
			break;
		case WALKING:
			speed.x = (this.left?-1f:1f)*speedf*WALK_SPEED;
			if(this.time2<this.time){
				if(this.wait){
					this.wait = false;
					int randi = Utils.nextInt(100);
					if(randi<60){ //0-59 - 60%
						boolean nextSide = Utils.nextBoolean();
						this.left = nextSide;
						this.state = State.STILL;
					}else if(randi<90){ //60-89 - 30%
						boolean nextSide = Utils.nextBoolean();
						this.turn = this.left != nextSide;
						this.left = nextSide;
						this.state = State.RUNNING;
					}else{ //90-99 - 10%
						this.spdy = -Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y-speedf*MIN_JUMP_SPEED_Y;
						this.maxspdy = Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y+speedf*MIN_JUMP_SPEED_Y;
						this.state = State.JUMPING;
					}
				}else{
					this.time2 = this.time+Utils.nextInt(MAX_WALKING_TIME)+MIN_STATE_TIME;
					this.wait = true;
				}
			}
			break;
		case RUNNING:
			speed.x = (this.left?-1f:1f)*speedf*RUN_SPEED;;
			if(this.time2<this.time){
				if(this.wait){
					this.wait = false;
					int randi = Utils.nextInt(100);
					if(randi<50){ //0-49 - 50%
						boolean nextSide = Utils.nextBoolean();
						this.turn = this.left != nextSide;
						this.left = nextSide;
						this.state = State.WALKING;
					}else if(randi<90){ //50-89 - 40%
						this.spdy = -Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y-speedf*MIN_JUMP_SPEED_Y;
						this.maxspdy = Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y+speedf*MIN_JUMP_SPEED_Y;
						this.state = State.JUMPING;
					}else{ //90-99 - 10%
						this.state = State.STILL;
					}
				}else{
					this.time2 = this.time+Utils.nextInt(MAX_RUNNING_TIME)+MIN_STATE_TIME;
					this.wait = true;
				}
			}
			break;
		case LOOSING:
			if(this.time-this.time2>100){
				int randi = Utils.nextInt(100);
				if(randi<80){ //0-79 - 80%
					this.time2 = this.time+Utils.nextInt(MAX_STILL_TIME)+MIN_STATE_TIME;
					this.wait = true;
					this.state = State.STILL_DUCK;
				}else{ //80-99 - 20%
					this.wait = false;
					this.state = State.RUNNING;
				}
			}
			break;
		}
		
		if(this.state != State.LOOSING){
			this.pos = Utils.add(this.pos, speed);
			
			if(pos.x<=minx && this.left || pos.x+sizex>=maxx && !this.left){
				this.turn = true;
				this.left = !this.left;
			}
			
			if(this.state != State.JUMPING){
				int[] ybounds = Utils.getYBounds((int) pos.x);
				if(pos.y<=ybounds[0]){
					this.spdy = 0;
					this.maxspdy = Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y+speedf*MIN_JUMP_SPEED_Y;
					this.state = State.JUMPING;
				}else if(pos.y+sizey>ybounds[1]){
					this.spdy = -Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y-speedf*MIN_JUMP_SPEED_Y;
					this.maxspdy = -Math.abs(this.spdy);
					this.state = State.JUMPING;
				}
			}
			
			if(this.spdy<=0 && (this.state == State.JUMPING || this.state == State.RUNNING)){
				Rectangle bounds = this.getBounds();
				for(MarioAI ma:getOthers(this.id)){
					if(ma.spdy<=0 && !ma.isInvicible() && bounds.intersects(ma.getBounds())){
						int randi = Utils.nextInt(100);
						if(randi<50){
							ma.fall();
							ma.setKicked(true);
						}
					}
				}
			}
			
		}

		return new Point((int)this.pos.x, (int)this.pos.y);
	}
	
	public void jump(boolean left){
		this.turn = false;
		this.left = left;
		this.time2 = this.time+Utils.nextInt(MAX_WALKING_TIME);
		this.wait = false;
		this.spdy = -Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y-speedf*MIN_JUMP_SPEED_Y;
		this.maxspdy = Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y+speedf*MIN_JUMP_SPEED_Y;
		this.state = State.JUMPING;
	}
	
	public void fall(){
		int[] ybounds = Utils.getYBounds((int)this.pos.x);
		if(this.pos.y-this.sizey<ybounds[1]){
			this.turn = false;
			this.wait = false;
			this.maxspdy = Utils.nextFloat()*speedf*MAX_JUMP_SPEED_Y+speedf*MIN_JUMP_SPEED_Y;
			this.spdy = 0;
			this.state = State.JUMPING;
		}
	}
	
	//getter/setter
	
	public void setSize(int sizex, int sizey, int speedf){
		this.sizex = sizex;
		this.sizey = sizey;
		this.speedf = speedf;
	}
	
	public void setPos(Point newpos){
		this.pos = new Point2D.Float(newpos.x, newpos.y);
	}
	
	public int getTile(){
		if(this.turn){
			if(this.time%200>=100){
				this.turn = false;
			}
			return MARIO_TURNING;
		}else{
			switch(this.state){
			case LOOSING:
				return MARIO_LOOSING;
			case WALKING:
				return this.time%400<200?MARIO_STILL:MARIO_WALKING_2;
			case RUNNING:
				return this.time%150<75?MARIO_RUNNING_1:MARIO_RUNNING_2;
			case JUMPING:
				return this.spdy<=MAX_JUMP_SPEED_Y/10f?MARIO_JUMPING:this.kicked?MARIO_LOOSING:MARIO_FALLING;
			case STILL_LOOK_UP:
				return MARIO_LOOK_UP;
			case STILL_WIN:
				return MARIO_WIN;
			case STILL_BACK:
				return MARIO_BACK;
			case STILL_DUCK:
				return MARIO_DUCK;
			case STILL:
			default:
				return MARIO_STILL;
			}
		}
	}
	
	public boolean isReversed(){
		return this.left;
	}
	
	public Rectangle getBounds(){
		return new Rectangle((int)pos.x,(int)pos.y,sizex,sizey);
	}

	public int getId() {
		return id;
	}
	
	public float getSpdy() {
		return spdy;
	}

	public boolean isInvicible() {
		return invicible;
	}

	public void setInvicible(boolean invicible) {
		this.invicible = invicible;
	}

	public boolean isKicked() {
		return kicked;
	}

	public void setKicked(boolean kicked) {
		this.kicked = kicked;
	}

	//static functions
	
	public static List<MarioAI> getOthers(int id){
		List<MarioAI> list = new ArrayList<MarioAI>();
		for(MarioWindow mw:MarioWindow.getAll())
			if(mw.getAi().getId() != id)
				list.add(mw.getAi());
		return list;
	}
	
}
