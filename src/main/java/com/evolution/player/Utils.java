package com.evolution.player;

import java.util.Map;

public class Utils
{
  public static Position getPostionFromMap( Map< String, Object > data )
  {
    float x = ( (Long) data.get( "x" ) ).floatValue();
    float y = ( (Long) data.get( "y" ) ).floatValue();
    float z = ( (Long) data.get( "z" ) ).floatValue();
    return new Position( x, y, z );
  }
}
