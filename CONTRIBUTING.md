# Contributing to PKPrac

Thanks for wanting to contribute to PKPrac! Here's everything you need to know to get started.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/xekek/PKPrac.git`
3. Set up the development environment:
   ```bash
   ./gradlew setupDecompWorkspace
   ./gradlew idea  # or ./gradlew eclipse
   ```

## Development Guidelines

### Code Style
- Follow standard Java naming conventions
- Use descriptive variable and method names
- Comment complex logic so others can understand it
- Keep the existing code structure and patterns

### License Headers
All new files need the GPL v3 license header:
```java
/*
 * PKPrac - A parkour practice mod
 * Copyright (C) 2025 xeepy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
```

### Testing
- Test your changes in-game before submitting
- Make sure it works with Minecraft 1.8.9 and Forge 11.15.1.2318
- Test in both singleplayer and multiplayer environments

## Submitting Changes

1. Create a branch for your feature: `git checkout -b feature-name`
2. Make your changes with clear commit messages
3. Push to your fork: `git push origin feature-name`
4. Open a Pull Request with:
   - Clear description of what you changed
   - Why the changes are needed
   - What testing you did

## Reporting Issues

When reporting bugs, include:
- Minecraft version
- Forge version
- PKPrac version
- Steps to reproduce the issue
- What you expected vs what actually happened
- Crash logs if applicable

[open an issue](../../issues)

## Feature Requests

For new features:
- Check if it's already been requested
- Explain what you want and why it would be useful
- Keep in mind the scope of the mod

## Code of Conduct

- Be respectful in discussions
- Stay constructive with feedback
- Focus on improving the mod
- Help others when you can

Thanks for contributing!
