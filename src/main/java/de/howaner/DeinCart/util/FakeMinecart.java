package de.howaner.DeinCart.util;

import de.howaner.DeinCart.DeinCartPlugin;
import java.lang.reflect.Field;
import net.minecraft.server.v1_7_R3.AxisAlignedBB;
import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.BlockMinecartTrackAbstract;
import net.minecraft.server.v1_7_R3.Blocks;
import net.minecraft.server.v1_7_R3.DamageSource;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityMinecartAbstract;
import net.minecraft.server.v1_7_R3.EntityMinecartRideable;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Vec3D;
import net.minecraft.server.v1_7_R3.World;
import net.minecraft.server.v1_7_R3.WorldServer;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

public class FakeMinecart extends EntityMinecartRideable {
	private int startTimer = 40; //2 Sekunden

	public FakeMinecart(World world) {
		super(world);
	}
	
	public FakeMinecart(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.maxSpeed = 0.6F;
	}
	
	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (this.isInvulnerable()) {
			return false;
		}
		
		//No Damage :D
		return true;
	}
	
	@Override
	public void collide(Entity entity) {
		//No collide :D
	}
	
	@Override
	public AxisAlignedBB h(Entity entity) {
		return null;
	}
	
	@Override
	public void h() {
		double prevX = this.locX;
		double prevY = this.locY;
		double prevZ = this.locZ;
		
		if ((this.passenger != null) && (this.passenger.dead)) {
			if (this.passenger.vehicle == this) {
				this.passenger.vehicle = null;
			}
			
			this.passenger = null;
			
			RoutePlayer player = DeinCartPlugin.getManager().getPlayerFromMinecart((Minecart)this.getBukkitEntity());
			DeinCartPlugin.getManager().stopRoute(player.getPlayer(), false);
		}
		
		if (getType() > 0) {
			c(getType() - 1);
		}
		
		if (getDamage() > 0.0F) {
			setDamage(getDamage() - 1.0F);
		}
		
		if (this.locY < -64.0D) {
			F();
		}
		
		if ((!this.world.isStatic) && ((this.world instanceof WorldServer))) {
			this.world.methodProfiler.a("portal");
			
			int i = C();
			if (this.an) {
				if ((this.vehicle == null) && (this.ao++ >= i)) {
					this.ao = i;
					this.portalCooldown = ah();
					byte b0;
					if (this.world.worldProvider.dimension == -1)
						b0 = 0;
					else
						b0 = -1;
					
					b(b0);
				}
				this.an = false;
			} else {
				if (this.ao > 0) {
					this.ao -= 4;
				}
				
				if (this.ao < 0) {
					this.ao = 0;
				}
			}
			
			if (this.portalCooldown > 0) {
				this.portalCooldown -= 1;
			}
			this.world.methodProfiler.b();
		}
		
		if (this.world.isStatic) {
			int d = this.getPrivateValue("d", int.class);
			if (d > 0) {
				double d0 = this.locX + (this.getPrivateValue("e", double.class) - this.locX) / d;
				double d1 = this.locY + (this.getPrivateValue("f", double.class) - this.locY) / d;
				double d2 = this.locZ + (this.getPrivateValue("g", double.class) - this.locZ) / d;
				double d3 = MathHelper.g(this.getPrivateValue("h", double.class) - this.yaw);
				
				this.yaw = ((float)(this.yaw + d3 / d));
				this.pitch = ((float)(this.pitch + (this.getPrivateValue("i", double.class) - this.pitch) / d));
				this.setPrivateValue("d", d - 1);
				setPosition(d0, d1, d2);
				b(this.yaw, this.pitch);
			} else {
				setPosition(this.locX, this.locY, this.locZ);
				b(this.yaw, this.pitch);
			}
		} else {
			this.lastX = this.locX;
			this.lastY = this.locY;
			this.lastZ = this.locZ;
			this.motY -= 0.03999999910593033D;
			int j = MathHelper.floor(this.locX);
			
			int i = MathHelper.floor(this.locY);
			int k = MathHelper.floor(this.locZ);
			
			if (BlockMinecartTrackAbstract.b_(this.world, j, i - 1, k)) {
				i--;
			}
			
			double d4 = this.maxSpeed;
			double d5 = 0.0078125D;
			Block block = this.world.getType(j, i, k);
			
			if (BlockMinecartTrackAbstract.a(block)) {
				int l = this.world.getData(j, i, k);
				
				a(j, i, k, d4, d5, block, l);
				if (block == Blocks.ACTIVATOR_RAIL)
					a(j, i, k, (l & 0x8) != 0);
			}
			else {
				b(d4);
			}
			
			H();
			this.pitch = 0.0F;
			double d6 = this.lastX - this.locX;
			double d7 = this.lastZ - this.locZ;
			
			if (d6 * d6 + d7 * d7 > 0.001D) {
				this.yaw = ((float)(Math.atan2(d7, d6) * 180.0D / 3.141592653589793D));
				if (this.getPrivateValue("a", boolean.class)) {
					this.yaw += 180.0F;
				}
			}
			
			double d8 = MathHelper.g(this.yaw - this.lastYaw);
			
			if ((d8 < -170.0D) || (d8 >= 170.0D)) {
				this.yaw += 180.0F;
				this.setPrivateValue("a", !this.getPrivateValue("a", boolean.class));
			}
			
			b(this.yaw, this.pitch);
			
			//Finish Check
			int oldX = MathHelper.floor(prevX);
			int oldY = MathHelper.floor(prevY);
			int oldZ = MathHelper.floor(prevZ);
			
			int newX = MathHelper.floor(this.locX);
			int newY = MathHelper.floor(this.locY);
			int newZ = MathHelper.floor(this.locZ);
			
			if ((oldX != newX) || (oldY != newY) || (oldZ != newZ)) {
				if (this.world.getType(newX, newY - 1, newZ) == Blocks.GOLD_BLOCK) {
					DeinCartPlugin.getManager().stopRoute((Player)this.passenger.getBukkitEntity(), true);
				}
				
				this.world.getWorld().playEffect(new Location(this.world.getWorld(), oldX, oldY, oldZ), Effect.SMOKE, 2);
			}
		}
	}
	
	@Override
	public void a(int i, int j, int k, double d0, double d1, Block block, int l) {
		this.fallDistance = 0.0F;
		Vec3D vec3d = a(this.locX, this.locY, this.locZ);
		
		this.locY = j;
		boolean flag = false;
		boolean flag1 = false;
		
		if (block == Blocks.GOLDEN_RAIL) {
			flag = (l & 0x8) != 0;
			flag1 = !flag;
		}
		
		if (((BlockMinecartTrackAbstract)block).e()) {
			l &= 7;
		}
		
		if ((l >= 2) && (l <= 5)) {
			this.locY = (j + 1);
		}
		
		if (l == 2) {
			this.motX -= d1;
		}
		
		if (l == 3) {
			this.motX += d1;
		}
		
		if (l == 4) {
			this.motZ += d1;
		}
		
		if (l == 5) {
			this.motZ -= d1;
		}
		
		int[][][] matrix = this.getPrivateValue("matrix", int[][][].class);
		int[][] aint = matrix[l];
		double d2 = aint[1][0] - aint[0][0];
		double d3 = aint[1][2] - aint[0][2];
		double d4 = Math.sqrt(d2 * d2 + d3 * d3);
		double d5 = this.motX * d2 + this.motZ * d3;
		
		if (d5 < 0.0D) {
			d2 = -d2;
			d3 = -d3;
		}
		
		double d6 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
		
		if (d6 > 2.0D) {
			d6 = 2.0D;
		}
		
		this.motX = (d6 * d2 / d4);
		this.motZ = (d6 * d3 / d4);
		
		//Start Timer:
		if ((this.passenger != null) && (this.passenger instanceof EntityPlayer)) {
			RoutePlayer player = DeinCartPlugin.getManager().getPlayerFromMinecart((Minecart)this.getBukkitEntity());
			if (player != null) {
				double d8 = -Math.sin(player.getRoute().getStartDirection() * 3.141593F / 180.0F);
				double d9 = Math.cos(player.getRoute().getStartDirection() * 3.141593F / 180.0F);
				double d10 = this.motX * this.motX + this.motZ * this.motZ;
				if (d10 < 0.01D) {
					if (this.startTimer <= 0) {
						this.motX += d8 * 0.1D;
						this.motZ += d9 * 0.1D;
						flag1 = false;
					} else {
						--this.startTimer;
					}
				}
			}
		}
		
		
		/*if ((this.passenger != null) && ((this.passenger instanceof EntityLiving))) {
			double d7 = ((EntityLiving)this.passenger).be;
			if (d7 > 0.0D) {
				double d8 = -Math.sin(this.passenger.yaw * 3.141593F / 180.0F);
				double d9 = Math.cos(this.passenger.yaw * 3.141593F / 180.0F);
				double d10 = this.motX * this.motX + this.motZ * this.motZ;
				if (d10 < 0.01D) {
					this.motX += d8 * 0.1D;
					this.motZ += d9 * 0.1D;
					flag1 = false;
				}
			}
		}*/
		
		if (flag1) {
			double d7 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
			if (d7 < 0.03D) {
				this.motX *= 0.0D;
				this.motY *= 0.0D;
				this.motZ *= 0.0D;
			} else {
				this.motX *= 0.5D;
				this.motY *= 0.0D;
				this.motZ *= 0.5D;
			}
		}
		
		double d7 = 0.0D;
		double d8 = i + 0.5D + aint[0][0] * 0.5D;
		double d9 = k + 0.5D + aint[0][2] * 0.5D;
		double d10 = i + 0.5D + aint[1][0] * 0.5D;
		double d11 = k + 0.5D + aint[1][2] * 0.5D;
		
		d2 = d10 - d8;
		d3 = d11 - d9;
		
		if (d2 == 0.0D) {
			this.locX = (i + 0.5D);
			d7 = this.locZ - k;
		} else if (d3 == 0.0D) {
			this.locZ = (k + 0.5D);
			d7 = this.locX - i;
		} else {
			double d12 = this.locX - d8;
			double d13 = this.locZ - d9;
			d7 = (d12 * d2 + d13 * d3) * 2.0D;
		}
		
		this.locX = (d8 + d2 * d7);
		this.locZ = (d9 + d3 * d7);
		setPosition(this.locX, this.locY + this.height, this.locZ);
		double d12 = this.motX;
		double d13 = this.motZ;
		if (this.passenger != null) {
			d12 *= 0.75D;
			d13 *= 0.75D;
		}
		
		if (d12 < -d0) {
			d12 = -d0;
		}
		
		if (d12 > d0) {
			d12 = d0;
		}
		
		if (d13 < -d0) {
			d13 = -d0;
		}
		
		if (d13 > d0) {
			d13 = d0;
		}
		
		move(d12, 0.0D, d13);
		if ((aint[0][1] != 0) && (MathHelper.floor(this.locX) - i == aint[0][0]) && (MathHelper.floor(this.locZ) - k == aint[0][2]))
			setPosition(this.locX, this.locY + aint[0][1], this.locZ);
		else if ((aint[1][1] != 0) && (MathHelper.floor(this.locX) - i == aint[1][0]) && (MathHelper.floor(this.locZ) - k == aint[1][2]))
			setPosition(this.locX, this.locY + aint[1][1], this.locZ);
		
		i();
	}
	
	@Override
	public void i() {
		if ((this.passenger != null) || (!this.slowWhenEmpty)) {
			/*this.motX *= 0.996999979019165D;
			this.motY *= 0.0D;
			this.motZ *= 0.996999979019165D;*/
			this.motX *= 1.2D;
			this.motY *= 0.0D;
			this.motZ *= 1.2D;
		} else {
			this.motX *= 0.9599999785423279D;
			this.motY *= 0.0D;
			this.motZ *= 0.9599999785423279D;
		}
  }
	
	public <T extends Object> T getPrivateValue(String fieldName, Class<T> type) {
		try {
			Field field = EntityMinecartAbstract.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setPrivateValue(String fieldName, Object value) {
		try {
			Field field = EntityMinecartAbstract.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(this, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
