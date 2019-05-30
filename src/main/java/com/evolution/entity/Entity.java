package com.evolution.entity;

import java.util.List;

import com.evolution.Main;
import com.evolution.util.math.AxisAlignedBB;
import com.evolution.util.math.BlockPos;
import com.evolution.util.math.MathHelper;
import com.evolution.util.math.Vec3d;

public class Entity
{
  private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB( 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D );

  public double posX;
  public double posY;
  public double posZ;

  public double motionX;
  public double motionY;
  public double motionZ;

  public float yaw;
  public float pitch;
  public float pitchHead;

  public boolean onGround;
  private boolean sneaking;
  private boolean sprinting;

  private float width;
  private float height;

  private boolean isCollidedHorizontally;
  private boolean isCollidedVertically;
  private boolean isCollided;

  private float jumpMovementFactor = 0.02f;

  private float fallDistance;

  private AxisAlignedBB boundingBox;

  private int type;

  private float stepHeight;

  public Entity()
  {
    this.stepHeight = 0.6F;
    this.boundingBox = ZERO_AABB;
  }

  public void setType( int type )
  {
    this.type = type;
  }

  public int getType()
  {
    return this.type;
  }

  public boolean isSneaking()
  {
    return this.sneaking;
  }

  public void setBoundingBox( AxisAlignedBB box )
  {
    this.boundingBox = box;
  }

  public AxisAlignedBB getBoundingBox()
  {
    return this.boundingBox;
  }

  /**
   * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
   */
  public void setPosition( double x, double y, double z )
  {
    this.posX = x;
    this.posY = y;
    this.posZ = z;
    float f = this.width / 2.0F;
    float f1 = this.height;
    this.setBoundingBox( new AxisAlignedBB( x - f, y, z - f, x + f, y + f1, z + f ) );
    this.boundingBox.expandXyz( 0.01D );
  }

  /**
   * Sets the width and height of the entity.
   */
  protected void setSize( float width, float height )
  {
    if ( width != this.width || height != this.height )
    {
      float f = this.width;
      this.width = width;
      this.height = height;

      if ( this.width < f )
      {
        double d0 = width / 2.0D;
        this.setBoundingBox(
            new AxisAlignedBB( this.posX - d0, this.posY, this.posZ - d0, this.posX + d0, this.posY + this.height, this.posZ + d0 ) );
        return;
      }

      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.setBoundingBox( new AxisAlignedBB( axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + this.width,
          axisalignedbb.minY + this.height, axisalignedbb.minZ + this.width ) );
    }
  }

  protected float getJumpUpwardsMotion()
  {
    return 0.42F;
  }

  public boolean isSprinting()
  {
    return this.sprinting;
  }

  /**
   * Causes this entity to do an upwards motion (jumping).
   */
  protected void jump()
  {
    this.motionY = this.getJumpUpwardsMotion();

    if ( this.isSprinting() )
    {
      float f = this.yaw * 0.017453292F;
      this.motionX -= Math.sin( f ) * 0.2F;
      this.motionZ += Math.cos( f ) * 0.2F;
    }

    double d1 = this.motionX * this.motionX + this.motionZ * this.motionZ;

    if ( d1 < 0.010000000000000002D )
    {
      this.setMotionRelative( 0.0F, 0.0F, 1.0F, 0.1F );
    }
  }

  public void setMotionRelative( float forward, float upwards, float strafe, float f0 )
  {
    float f = forward * forward + upwards * upwards + strafe * strafe;

    if ( f >= 1.0E-4F )
    {
      f = (float) Math.sqrt( f );

      if ( f < 1.0F )
      {
        f = 1.0F;
      }

      f = f0 / f;
      forward = forward * f;
      upwards = upwards * f;
      strafe = strafe * f;
      float f1 = (float) Math.sin( this.yaw * 0.017453292F );
      float f2 = (float) Math.cos( this.yaw * 0.017453292F );
      this.motionX += forward * f2 - strafe * f1;
      this.motionY += upwards;
      this.motionZ += strafe * f2 + forward * f1;
    }
  }

  /**
   * Tries to move the entity towards the specified location.
   */
  public void moveEntity( MoverType x, double forward, double strafe, double upwards )
  {
    double d10 = this.posX;
    double d11 = this.posY;
    double d1 = this.posZ;

    // if ( this.isInWeb )
    // {
    // this.isInWeb = false;
    // motionX *= 0.25D;
    // motionY *= 0.05000000074505806D;
    // motionZ *= 0.25D;
    // this.motionX = 0.0D;
    // this.motionY = 0.0D;
    // this.motionZ = 0.0D;
    // }

    double d2 = forward;
    double d3 = strafe;
    double d4 = upwards;

    if ( ( x == MoverType.SELF || x == MoverType.PLAYER ) && this.onGround && this.isSneaking() && this instanceof Player )
    {
      for ( double d5 = 0.05D;
          forward != 0.0D
              && Main.WORLD.getCollisionBoxes( this, this.getBoundingBox().offset( forward, ( -this.stepHeight ), 0.0D ) ).isEmpty();
          d2 = forward )
      {
        if ( forward < 0.05D && forward >= -0.05D )
        {
          forward = 0.0D;
        }
        else if ( forward > 0.0D )
        {
          forward -= 0.05D;
        }
        else
        {
          forward += 0.05D;
        }
      }

      for ( ;
          upwards != 0.0D
              && Main.WORLD.getCollisionBoxes( this, this.getBoundingBox().offset( 0.0D, ( -this.stepHeight ), upwards ) ).isEmpty();
          d4 = upwards )
      {
        if ( upwards < 0.05D && upwards >= -0.05D )
        {
          upwards = 0.0D;
        }
        else if ( upwards > 0.0D )
        {
          upwards -= 0.05D;
        }
        else
        {
          upwards += 0.05D;
        }
      }

      for ( ;
          forward != 0.0D && upwards != 0.0D
              && Main.WORLD.getCollisionBoxes( this, this.getBoundingBox().offset( forward, ( -this.stepHeight ), upwards ) ).isEmpty();
          d4 = upwards )
      {
        if ( forward < 0.05D && forward >= -0.05D )
        {
          forward = 0.0D;
        }
        else if ( forward > 0.0D )
        {
          forward -= 0.05D;
        }
        else
        {
          forward += 0.05D;
        }

        d2 = forward;

        if ( upwards < 0.05D && upwards >= -0.05D )
        {
          upwards = 0.0D;
        }
        else if ( upwards > 0.0D )
        {
          upwards -= 0.05D;
        }
        else
        {
          upwards += 0.05D;
        }
      }
    }

    List< AxisAlignedBB > list1 = Main.WORLD.getCollisionBoxes( this, this.getBoundingBox().addCoord( forward, strafe, upwards ) );
    AxisAlignedBB axisalignedbb = this.getBoundingBox();

    if ( strafe != 0.0D )
    {
      int k = 0;

      for ( int l = list1.size(); k < l; ++k )
      {
        strafe = list1.get( k ).calculateYOffset( this.getBoundingBox(), strafe );
      }

      this.setBoundingBox( this.getBoundingBox().offset( 0.0D, strafe, 0.0D ) );
    }

    if ( forward != 0.0D )
    {
      int j5 = 0;

      for ( int l5 = list1.size(); j5 < l5; ++j5 )
      {
        forward = list1.get( j5 ).calculateXOffset( this.getBoundingBox(), forward );
      }

      if ( forward != 0.0D )
      {
        this.setBoundingBox( this.getBoundingBox().offset( forward, 0.0D, 0.0D ) );
      }
    }

    if ( upwards != 0.0D )
    {
      int k5 = 0;

      for ( int i6 = list1.size(); k5 < i6; ++k5 )
      {
        upwards = list1.get( k5 ).calculateZOffset( this.getBoundingBox(), upwards );
      }

      if ( upwards != 0.0D )
      {
        this.setBoundingBox( this.getBoundingBox().offset( 0.0D, 0.0D, upwards ) );
      }
    }

    boolean flag = this.onGround || d3 != strafe && d3 < 0.0D;

    if ( this.stepHeight > 0.0F && flag && ( d2 != forward || d4 != upwards ) )
    {
      double d14 = forward;
      double d6 = strafe;
      double d7 = upwards;
      AxisAlignedBB axisalignedbb1 = this.getBoundingBox();
      this.setBoundingBox( axisalignedbb );
      strafe = this.stepHeight;
      List< AxisAlignedBB > list = Main.WORLD.getCollisionBoxes( this, this.getBoundingBox().addCoord( d2, strafe, d4 ) );
      AxisAlignedBB axisalignedbb2 = this.getBoundingBox();
      AxisAlignedBB axisalignedbb3 = axisalignedbb2.addCoord( d2, 0.0D, d4 );
      double d8 = strafe;
      int j1 = 0;

      for ( int k1 = list.size(); j1 < k1; ++j1 )
      {
        d8 = list.get( j1 ).calculateYOffset( axisalignedbb3, d8 );
      }

      axisalignedbb2 = axisalignedbb2.offset( 0.0D, d8, 0.0D );
      double d18 = d2;
      int l1 = 0;

      for ( int i2 = list.size(); l1 < i2; ++l1 )
      {
        d18 = list.get( l1 ).calculateXOffset( axisalignedbb2, d18 );
      }

      axisalignedbb2 = axisalignedbb2.offset( d18, 0.0D, 0.0D );
      double d19 = d4;
      int j2 = 0;

      for ( int k2 = list.size(); j2 < k2; ++j2 )
      {
        d19 = list.get( j2 ).calculateZOffset( axisalignedbb2, d19 );
      }

      axisalignedbb2 = axisalignedbb2.offset( 0.0D, 0.0D, d19 );
      AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
      double d20 = strafe;
      int l2 = 0;

      for ( int i3 = list.size(); l2 < i3; ++l2 )
      {
        d20 = list.get( l2 ).calculateYOffset( axisalignedbb4, d20 );
      }

      axisalignedbb4 = axisalignedbb4.offset( 0.0D, d20, 0.0D );
      double d21 = d2;
      int j3 = 0;

      for ( int k3 = list.size(); j3 < k3; ++j3 )
      {
        d21 = list.get( j3 ).calculateXOffset( axisalignedbb4, d21 );
      }

      axisalignedbb4 = axisalignedbb4.offset( d21, 0.0D, 0.0D );
      double d22 = d4;
      int l3 = 0;

      for ( int i4 = list.size(); l3 < i4; ++l3 )
      {
        d22 = list.get( l3 ).calculateZOffset( axisalignedbb4, d22 );
      }

      axisalignedbb4 = axisalignedbb4.offset( 0.0D, 0.0D, d22 );
      double d23 = d18 * d18 + d19 * d19;
      double d9 = d21 * d21 + d22 * d22;

      if ( d23 > d9 )
      {
        forward = d18;
        upwards = d19;
        strafe = -d8;
        this.setBoundingBox( axisalignedbb2 );
      }
      else
      {
        forward = d21;
        upwards = d22;
        strafe = -d20;
        this.setBoundingBox( axisalignedbb4 );
      }

      int j4 = 0;

      for ( int k4 = list.size(); j4 < k4; ++j4 )
      {
        strafe = list.get( j4 ).calculateYOffset( this.getBoundingBox(), strafe );
      }

      this.setBoundingBox( this.getBoundingBox().offset( 0.0D, strafe, 0.0D ) );

      if ( d14 * d14 + d7 * d7 >= forward * forward + upwards * upwards )
      {
        forward = d14;
        strafe = d6;
        upwards = d7;
        this.setBoundingBox( axisalignedbb1 );
      }
    }

    this.resetPositionToBB();
    this.isCollidedHorizontally = d2 != forward || d4 != upwards;
    this.isCollidedVertically = d3 != strafe;
    this.onGround = this.isCollidedVertically && d3 < 0.0D;
    this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
    int j6 = (int) Math.floor( this.posX );
    int i1 = (int) Math.floor( this.posY - 0.20000000298023224D );
    int k6 = (int) Math.floor( this.posZ );
    BlockPos blockpos = new BlockPos( j6, i1, k6 );
    this.updateFallState( strafe, this.onGround, blockpos );

    if ( d2 != forward )
    {
      this.motionX = 0.0D;
    }

    if ( d4 != upwards )
    {
      this.motionZ = 0.0D;
    }
  }

  public boolean isElytraFlying()
  {
    return false;
  }

  /**
   * returns a (normalized) vector of where this entity is looking
   */
  public Vec3d getLookVec()
  {
    return this.getVectorForRotation( this.pitch, this.yaw );
  }

  /**
   * Creates a Vec3 using the pitch and yaw of the entities rotation.
   */
  protected final Vec3d getVectorForRotation( float pitch, float yaw )
  {
    float f = (float) Math.cos( -yaw * 0.017453292F - (float) Math.PI );
    float f1 = (float) Math.sin( -yaw * 0.017453292F - (float) Math.PI );
    float f2 = -(float) Math.cos( -pitch * 0.017453292F );
    float f3 = (float) Math.sin( -pitch * 0.017453292F );
    return new Vec3d( f1 * f2, f3, f * f2 );
  }

  public boolean isInWater()
  {
    return false;
  }

  public boolean isInLava()
  {
    return false;
  }

  public void travel( float forwards, float upwards, float strafe )
  {
    if ( !this.isInWater() )
    {
      if ( !this.isInLava() )
      {
        if ( this.isElytraFlying() )
        {
          if ( this.motionY > -0.5D )
          {
            this.fallDistance = 1.0F;
          }

          Vec3d vec3d = this.getLookVec();
          float f = this.pitch * 0.017453292F;
          double d6 = Math.sqrt( vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord );
          double d8 = Math.sqrt( this.motionX * this.motionX + this.motionZ * this.motionZ );
          double d1 = vec3d.lengthVector();
          float f4 = (float) Math.cos( f );
          f4 = (float) ( (double) f4 * (double) f4 * Math.min( 1.0D, d1 / 0.4D ) );
          this.motionY += -0.08D + f4 * 0.06D;

          if ( this.motionY < 0.0D && d6 > 0.0D )
          {
            double d2 = this.motionY * -0.1D * f4;
            this.motionY += d2;
            this.motionX += vec3d.xCoord * d2 / d6;
            this.motionZ += vec3d.zCoord * d2 / d6;
          }

          if ( f < 0.0F )
          {
            double d10 = d8 * ( -(float) Math.sin( f ) ) * 0.04D;
            this.motionY += d10 * 3.2D;
            this.motionX -= vec3d.xCoord * d10 / d6;
            this.motionZ -= vec3d.zCoord * d10 / d6;
          }

          if ( d6 > 0.0D )
          {
            this.motionX += ( vec3d.xCoord / d6 * d8 - this.motionX ) * 0.1D;
            this.motionZ += ( vec3d.zCoord / d6 * d8 - this.motionZ ) * 0.1D;
          }

          this.motionX *= 0.9900000095367432D;
          this.motionY *= 0.9800000190734863D;
          this.motionZ *= 0.9900000095367432D;
          this.moveEntity( MoverType.SELF, this.motionX, this.motionY, this.motionZ );
        }
        else
        {
          float f6 = 0.91F;
          BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos =
              BlockPos.PooledMutableBlockPos.retain( this.posX, this.getBoundingBox().minY - 1.0D, this.posZ );

          if ( this.onGround )
          {
            f6 = Main.WORLD.getBlockSlipperiness( Main.WORLD.getBlock( blockpos$pooledmutableblockpos ) ) * 0.91F;
          }

          float f7 = 0.16277136F / ( f6 * f6 * f6 );
          float f8;

          if ( this.onGround )
          {
            f8 = this.getMovementSpeed() * f7;
          }
          else
          {
            f8 = this.jumpMovementFactor;
          }

          this.setMotionRelative( forwards, upwards, strafe, f8 );
          f6 = 0.91F;

          if ( this.onGround )
          {
            f6 = Main.WORLD.getBlockSlipperiness(
                Main.WORLD.getBlock( blockpos$pooledmutableblockpos.setPos( this.posX, this.getBoundingBox().minY - 1.0D, this.posZ ) ) ) * 0.91F;
          }

          if ( this.isOnLadder() )
          {
            float f9 = 0.15F;
            this.motionX = MathHelper.clamp( this.motionX, -0.15000000596046448D, 0.15000000596046448D );
            this.motionZ = MathHelper.clamp( this.motionZ, -0.15000000596046448D, 0.15000000596046448D );
            this.fallDistance = 0.0F;

            if ( this.motionY < -0.15D )
            {
              this.motionY = -0.15D;
            }

            boolean flag = this.isSneaking() && this instanceof Player;

            if ( flag && this.motionY < 0.0D )
            {
              this.motionY = 0.0D;
            }
          }

          this.moveEntity( MoverType.SELF, this.motionX, this.motionY, this.motionZ );

          if ( this.isCollidedHorizontally && this.isOnLadder() )
          {
            this.motionY = 0.2D;
          }

          // if ( this.isPotionActive( MobEffects.LEVITATION ) )
          // {
          // this.motionY += ( 0.05D * (double) ( this.getActivePotionEffect( MobEffects.LEVITATION ).getAmplifier() + 1
          // ) - this.motionY ) * 0.2D;
          // }
          blockpos$pooledmutableblockpos.setPos( this.posX, 0.0D, this.posZ );

          if ( Main.WORLD.isBlockLoaded( blockpos$pooledmutableblockpos ) )
          {
            this.motionY -= 0.08D;
          }
          else if ( this.posY > 0.0D )
          {
            this.motionY = -0.1D;
          }
          else
          {
            this.motionY = 0.0D;
          }

          this.motionY *= 0.9800000190734863D;
          this.motionX *= f6;
          this.motionZ *= f6;
          blockpos$pooledmutableblockpos.release();
        }
      }
      else
      {
        double d4 = this.posY;
        this.setMotionRelative( forwards, upwards, strafe, 0.02F );
        this.moveEntity( MoverType.SELF, this.motionX, this.motionY, this.motionZ );
        this.motionX *= 0.5D;
        this.motionY *= 0.5D;
        this.motionZ *= 0.5D;

        this.motionY -= 0.02D;

        // if ( this.isCollidedHorizontally
        // && this.isOffsetPositionInLiquid( this.motionX, this.motionY + 0.6000000238418579D - this.posY + d4,
        // this.motionZ ) )
        // {
        // this.motionY = 0.30000001192092896D;
        // }
      }
    }
    else
    {
      double d0 = this.posY;
      // float f1 = this.getWaterSlowDown();
      float f2 = 0.02F;
      float f3 = 0.0f;

      if ( f3 > 3.0F )
      {
        f3 = 3.0F;
      }

      if ( !this.onGround )
      {
        f3 *= 0.5F;
      }

      // if ( f3 > 0.0F )
      // {
      // f1 += ( 0.54600006F - f1 ) * f3 / 3.0F;
      // f2 += ( this.getAIMoveSpeed() - f2 ) * f3 / 3.0F;
      // }

      this.setMotionRelative( forwards, upwards, strafe, f2 );
      this.moveEntity( MoverType.SELF, this.motionX, this.motionY, this.motionZ );
      // this.motionX *= f1;
      // this.motionY *= 0.800000011920929D;
      // this.motionZ *= f1;

      this.motionY -= 0.02D;

      // if ( this.isCollidedHorizontally
      // && this.isOffsetPositionInLiquid( this.motionX, this.motionY + 0.6000000238418579D - this.posY + d0,
      // this.motionZ ) )
      // {
      // this.motionY = 0.30000001192092896D;
      // }
    }
  }

  /**
   * Resets the entity's position to the center (planar) and bottom (vertical) points of its bounding box.
   */
  public void resetPositionToBB()
  {
    AxisAlignedBB axisalignedbb = this.getBoundingBox();
    this.posX = ( axisalignedbb.minX + axisalignedbb.maxX ) / 2.0D;
    this.posY = axisalignedbb.minY;
    this.posZ = ( axisalignedbb.minZ + axisalignedbb.maxZ ) / 2.0D;
  }

  protected void updateFallState( double y, boolean onGroundIn, BlockPos pos )
  {
    if ( onGroundIn )
    {
      this.fallDistance = 0.0F;
    }
    else if ( y < 0.0D )
    {
      this.fallDistance = (float) ( this.fallDistance - y );
    }
  }

  public float getMovementSpeed()
  {
    return 0.69999f;
  }

  public boolean isOnLadder()
  {
    return false;
  }
}
