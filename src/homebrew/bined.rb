cask "bined" do
  version "0.2.5"
  sha256 "580a20383d4e7aa9a2504a89bbf3d81a89eedce19a3d7f1ef44a76e0bf04ad69"

  url "https://bined.exbin.org/download/?f=bined-#{version}.dmg"
  name "BinEd"
  desc "Free and open source hex viewer/editor written in Java"
  homepage "https://bined.exbin.org/editor"
  
  livecheck do
    url "https://bined.exbin.org/update/homebrew"
    regex(/^v?(\d+(?:\.\d+)+)$/i)
  end

  app "BinEd.app"

  zap trash: "~/Library/Preferences/org.exbin.bined.plist"
end
