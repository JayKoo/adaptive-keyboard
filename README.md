# Adaptive Keyboard

Privacy-first Android IME PoC that learns the user's touch bias and adapts invisible hit areas without moving visible keys.

## v0.2
- English QWERTY + Korean 2-beolsik toggle
- Basic Hangul composition (initial/medial/final + common compound vowels/finals)
- Per-key adaptive touch centers (EMA)
- Backspace + quick replacement as implicit correction feedback
- Optional live touch heatmap overlay
- Keystroke / correction count / correction-rate metrics
- Local-only storage; no INTERNET permission

## Adaptive algorithm
Each physical QWERTY position stores learned `(dx,dy)`. English and Korean share the same physical-key touch model, so adaptation follows the user's finger geometry rather than language.

`score(k) = (x-(cx+dx))² + (y-(cy+dy))²`

## Galaxy setup
1. Open in Android Studio with JDK 17 and sync Gradle.
2. Run `app` on Android 8+.
3. Open Adaptive Keyboard → **Open keyboard settings** → enable it.
4. Tap **Choose keyboard** → Adaptive Keyboard.
5. Use **한/영** on the keyboard to switch languages.
6. Enable **Touch heatmap overlay** in the app to visualize recent touch locations.

## Evaluation
Compare correction rate before/after adaptation. The current metric is:
`correction rate = inferred quick corrections / keystrokes × 100`

This is a PoC proxy, not yet a rigorous typo-rate measure.

## Known limitations
- Shift/double consonants (ㄲ/ㄸ/ㅃ/ㅆ/ㅉ), punctuation, symbols and candidate suggestions are not yet implemented.
- Hangul composition covers the common composition path but needs broader edge-case tests.
- Correction attribution is intentionally simple and can misclassify intentional edits.
- Heatmap currently shows recent touches during the active keyboard session.

## Next milestones
Shift/double consonants · punctuation/symbol layer · robust Hangul tests · A/B experiment mode · per-key heatmap/statistics · one-/two-hand and orientation profiles · optional context-aware inference.
