//
//  iPodCommand.m
//  HelloPhoneGap
//
//  Created by Hiedi Utley on 4/1/11.
//  Copyright 2011 Chariot Solutions, LLC. All rights reserved.
//

#import "MediaLibrary.h"
#import <MediaPlayer/MediaPlayer.h>

@implementation MediaLibrary
@synthesize player;

// Configures and displays the media item picker.
- (void) showMediaPicker :(CDVInvokedUrlCommand *)command {
    
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
    
    if(player == nil){
        player = [MPMusicPlayerController systemMusicPlayer];
    }
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    
    [rootViewController dismissViewControllerAnimated:YES completion:nil ];
    
    [player setQueueWithItemCollection:mediaItemCollection];
    [player play];
}


// Responds to the user tapping done having chosen no music.
- (void) mediaPickerDidCancel: (MPMediaPickerController *) mediaPicker {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    [rootViewController dismissViewControllerAnimated:YES completion:nil ];
}


@end
