package io.github.IBeHunting.IgnitedPotions.Config;

import java.io.File;

public interface PluginConfig
{
   File getFile();
   void save();
   void load();
}
