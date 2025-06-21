# PKPrac - Parkour Practice Mod
![PKPrac](https://files.catbox.moe/4jfog3.png)

A comprehensive parkour practice suite mod for Minecraft 1.8.9 that helps players improve their parkour skills through advanced practice features.

You can teleport back like traditional parkour practice plugins but with no latency, advanced checkpoint management with importing and exporting, and saving of velocity so you can simulate jumps from midair perfectly

When you activate practice mode, your client movement is desynced from the server, meaning the server will see you at your anchor point. You can freely move and test jumps, then teleport back to your original location exactly as you left. (Check the [demo section](https://github.com/xeepy/PKPrac?tab=readme-ov-file#demos) for a better understanding.)

It also supports sending the AFK packets you normally would so it is completely anticheat friendly. The mod will also resync you if the block you are standing on is broken/modified, or if you get teleported or damaged/knockbacked as well.
There is configurable options for hotkeys, showing where your checkpoints and anchor are, automatically setting a checkpoint when you desync, and the scale of the gif that indicates you are in practice mode.


## Using This on Servers

This mod desyncs your movement client-side and is built with anticheat compatibility in mind. It should be safe on most servers, but there's no guarantee every anticheat will agree.
If you do get flagged, try reproducing it on an anticheat test server like `test.ccbluex.net` and report it (see [Reporting an Issue](https://github.com/xeepy/PKPrac/blob/main/CONTRIBUTING.md#reporting-issues)) to help improve the mod.

## For Server Administrators

Checkout the [companion plugin](https://github.com/xeepy/PKPrac-Companion)!

We have a companion plugin that allows admins to disable and enable practice mode, it uses invisible armor stands above the player from the server. This feature is designed to enhance server customization and gameplay experience.

Note: In a future update, we will transfer this plugin to a channel-based messaging system for improved communication and flexibility.

## Features

- **Practice Mode**: Press `default G` to desync your client movement and practice wherever you want, if auto checkpoint is on you can press `default R` to teleport back to the anchor point. You can also fly `default F` to jumps later on and set custom checkpoints `default Z`
- **Checkpoint Management**: Save as many checkpoints as you like `.setmaxcp [num]` and switch between them fast `default F9 and F10`. You can also save all of your checkpoints and load them back from .txt files in the PKPrac folder. For example, you can share 100 checkpoints from a long rankup course with a friend. Checkpoints can be placed midair and preserve your exact velocity tick perfectly, meaning you can simulate mid-air turns 1:1. (See the [Demos section](https://github.com/xeepy/PKPrac?tab=readme-ov-file#demos) for a visual example.)
- **Visual Feedback**: On-screen notifications and visual indicators
- **Desync Commands**: Use `.teleport` and `.angle` commands for precise positioning in desync mode

## Installation

1. Download the latest release from the [Releases](../../releases) page
2. Install Minecraft Forge 1.8.9-11.15.1.2318 or compatible version
3. Place the PKPrac JAR file in your `.minecraft/mods` folder
4. Launch Minecraft and enjoy!

 Dev builds are located [here](https://github.com/xeepy/PKPrac/actions)

## Demos

Desync Demo

[![Desync](https://img.youtube.com/vi/cL7oGmSdmg8/hqdefault.jpg)](https://youtube.com/v/cL7oGmSdmg8)

Checkpoint Manager Demo

[![Checkpoint](https://img.youtube.com/vi/XuQn9x2hGR4/hqdefault.jpg)](https://youtube.com/watch?v=XuQn9x2hGR4)

## Default Hotkeys

| Key | Action |
|-----|--------|
| `G` | Toggle Practice Mode |
| `P` | Open Settings GUI |
| `Z` | Save Checkpoint |
| `R` | Load Current Checkpoint |
| `F9` | Next Checkpoint |
| `F10` | Previous Checkpoint |
| `F` | Toggle Practice Flight |

*Note: All keybindings can be customized in Minecraft's controls menu under the "PKPrac" category.*

## Development

This mod is built using Minecraft Forge for 1.8.9. To set up the development environment:

```bash
git clone https://github.com/xeepy/PKPrac.git
cd PKPrac
./gradlew setupDecompWorkspace
./gradlew idea  # or ./gradlew eclipse
```

## License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0).

If you use any part of this code in your own project, your project must also be licensed under GPL-3.0 and made open-source. This is a copyleft license — it's not optional or negotiable. If you’re not okay with that, you cannot reuse this code.

See the [License](LICENSE) file for the full terms.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## Credits

- **Author**: xeepy
- **Special Thanks**: SptSSQ - logo, ideas, testing

## Support

If you encounter any issues or have suggestions, please [open an issue](../../issues) on GitHub.
