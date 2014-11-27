//
//  iPodCommand.m
//  HelloPhoneGap
//
//  Created by Hiedi Utley on 4/1/11.
//  Copyright 2011 Chariot Solutions, LLC. All rights reserved.
//

#import "MediaLibrary.h"
#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVAudioSession.h>

@implementation MediaLibrary

@synthesize player, isMediaSelected;

-(void)initPlayer
{
    if(self.player == nil){
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryAmbient error:nil];
        self.player = [MPMusicPlayerController systemMusicPlayer];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(musicPlayerPlayBackStatusChanged:)
                                                     name:MPMusicPlayerControllerPlaybackStateDidChangeNotification
                                                   object:nil];
    }
}

// Configures and displays the media item picker.
- (void) showMediaPicker :(CDVInvokedUrlCommand *)command {
    
    [self initPlayer];
    
	MPMediaPickerController *picker = [[MPMediaPickerController alloc] initWithMediaTypes: MPMediaTypeMusic];
	
    if (picker)
    {
        picker.delegate						= self;
        picker.allowsPickingMultipleItems	= YES;
        picker.prompt						= @"Select music to play.";
        
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        UIViewController *rootViewController = window.rootViewController;
        
        [rootViewController presentViewController: picker animated: YES completion:nil ];
        
    }
    else
    {
        UIAlertView * alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"You must be running on a device for this to work!" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        
        [alert show];
    }
}


// Responds to the user tapping Done after choosing music.
- (void) mediaPicker: (MPMediaPickerController *) mediaPicker didPickMediaItems: (MPMediaItemCollection *) mediaItemCollection {
    
    [self initPlayer];
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    
    [rootViewController dismissViewControllerAnimated:YES completion:nil ];
    
    [self.player setQueueWithItemCollection:mediaItemCollection];
    //[self.player play];
    self.isMediaSelected = YES;
}


// Responds to the user tapping done having chosen no music.
- (void) mediaPickerDidCancel: (MPMediaPickerController *) mediaPicker {
    
    [self initPlayer];
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    [rootViewController dismissViewControllerAnimated:YES completion:nil ];
}

- (void) play:(CDVInvokedUrlCommand *)command {
    [self initPlayer];
    if(self.isMediaSelected)[self.player play];
}

- (void) pause:(CDVInvokedUrlCommand *)command {
    [self initPlayer];
    if(self.isMediaSelected)[self.player pause];
}

-(void)musicPlayerPlayBackStatusChanged:(NSNotification *)notification
{
    NSLog(@"%ld", self.player.playbackState);
}
                  


@end
