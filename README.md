# Adaptive Keyboard

A privacy-first Android IME proof-of-concept that learns a user's touch bias and adapts the **invisible hit areas** while keeping the visible keyboard fixed.

## v0.1
- Android system IME with `InputMethodService`
- Custom QWERTY keyboard
- Per-key adaptive touch center using EMA
- Backspace + quick replacement as implicit correction feedback
- Adaptive ON/OFF and model reset
- Local-only learning; no INTERNET permission

## Algorithm
Each key stores learned `(dx, dy)`. Selection minimizes:
`score(k) = (x-(cx+dx))² + (y-(cy+dy))²`.

## Build / Galaxy setup
1. Open in Android Studio with JDK 17 and sync Gradle.
2. Run the `app` configuration on Android 8+.
3. Open **Adaptive Keyboard**.
4. Tap **Open keyboard settings**, enable it, then **Choose keyboard**.
5. Type normally. When a wrong key is immediately deleted and replaced, repeated correction patterns adapt the invisible hit area.

## Current limitation
v0.1 uses English QWERTY to validate the learning loop. Korean 2-beolsik composition is the next milestone.

## Next
Korean 2-beolsik · touch heatmap · A/B correction-rate metrics · stronger correction attribution · orientation/hand profiles · optional context model.
