#!/bin/bash
set -e

# Use the PORT env var assigned by Railway, default to 8080
PORT="${PORT:-8080}"

echo "Starting Xvfb on display :1 with resolution 1280x720..."
Xvfb :1 -screen 0 1280x720x24 &
sleep 2

echo "Starting Fluxbox window manager..."
export DISPLAY=:1
fluxbox &
sleep 2

echo "Starting x11vnc..."
x11vnc -display :1 -nopw -forever -shared -rfbport 5900 &
sleep 2

echo "Starting noVNC and websockify proxy on port $PORT..."
# Symlink vnc.html to index.html in the noVNC directory so it loads automatically
ln -sf /usr/share/novnc/vnc.html /usr/share/novnc/index.html
websockify --web=/usr/share/novnc/ "$PORT" localhost:5900 &
sleep 2

echo "Starting JavaFX Application..."
mvn javafx:run
