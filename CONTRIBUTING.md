# Contributing to PKPrac

Thank you for your interest in contributing to PKPrac! This document provides guidelines for contributing to the project.

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
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Maintain the existing code structure and patterns

### License Headers
All new files must include the GPL v3 license header:
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
- Test your changes thoroughly in-game
- Ensure compatibility with Minecraft 1.8.9 and Forge 11.15.1.2318
- Test both single-player and multiplayer environments

## Submitting Changes

1. Create a new branch for your feature: `git checkout -b feature-name`
2. Make your changes and commit them with descriptive messages
3. Push to your fork: `git push origin feature-name`
4. Create a Pull Request with:
   - Clear description of changes
   - Why the changes are needed
   - Any testing performed

## Reporting Issues

When reporting bugs, please include:
- Minecraft version
- Forge version
- PKPrac version
- Steps to reproduce
- Expected vs actual behavior
- Crash logs (if applicable)

## Feature Requests

For new features:
- Check if the feature already exists or has been requested
- Explain the use case and benefits
- Consider implementation complexity

## Code of Conduct

- Be respectful and constructive
- Focus on the technical aspects
- Help others learn and grow

Thank you for contributing!
